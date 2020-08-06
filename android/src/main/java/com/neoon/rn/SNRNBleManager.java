package com.neoon.rn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.neoon.blesdk.core.ble.BaseUUID;
import com.neoon.blesdk.core.ble.BleHelper;
import com.neoon.blesdk.core.entity.BleDevice;
import com.neoon.blesdk.core.entity.FilePackage;
import com.neoon.blesdk.core.interfaces.OnScanBleListener;
import com.neoon.blesdk.core.interfaces.OnWallpaperUploadListener;
import com.neoon.blesdk.core.utils.BLELog;
import com.neoon.blesdk.encapsulation.ble.SNBLEHelper;
import com.neoon.blesdk.encapsulation.ble.SNBLESDK;
import com.neoon.blesdk.encapsulation.ble.SNBLEScanner;
import com.neoon.blesdk.encapsulation.cmd.SNCMD;
import com.neoon.blesdk.encapsulation.entity.SNBLEDevice;
import com.neoon.blesdk.encapsulation.entity.SNBLEEvent;
import com.neoon.blesdk.encapsulation.service.SNBLEService;
import com.neoon.blesdk.encapsulation.storage.DeviceStorage;
import com.neoon.blesdk.interfaces.OnDeviceCommRawDataListener;
import com.neoon.blesdk.interfaces.OnDeviceConnectListener;
import com.neoon.blesdk.interfaces.OnDeviceLeScanListener;
import com.neoon.blesdk.util.eventbus.SNEvent;
import com.neoon.blesdk.util.eventbus.SNEventBus;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



class SNRNBleManager extends ReactContextBaseJavaModule {

	public static final String LOG_TAG = "SNRNBleManager";
	private static final int ENABLE_REQUEST = 539;

	private BluetoothAdapter bluetoothAdapter;
	private BluetoothManager bluetoothManager;
	private Context context;
	private ReactApplicationContext reactContext;

	private boolean forceLegacy;

	private boolean isInitialized;
	public ReactApplicationContext getReactContext() {
		return reactContext;
	}


	public SNRNBleManager(ReactApplicationContext reactContext) {
		super(reactContext);
		context = reactContext;
		this.reactContext = reactContext;
		Log.d(LOG_TAG, "SNRNBleManager created");
	}

	@Override
	public String getName() {
		return "SNRNBleManager";
	}


	public void sendEvent(String eventName,
						  @Nullable WritableMap params) {
		BLELog.d("sendEvent===>>"+eventName+" "+params.toString());
		getReactApplicationContext()
				.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
				.emit(eventName, params);
	}

	public void sendEvent(String eventName,
						  @Nullable WritableArray params) {

		BLELog.d("sendEvent===>>"+eventName+" "+params.toString());
		getReactApplicationContext()
				.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
				.emit(eventName, params);
	}

	/** 16进制字符串转换成16进制byte数组，每两位转换 */
	public static byte[] strToHexByteArray(String str){
		byte[] hexByte = new byte[str.length()/2];
		for(int i = 0,j = 0; i < str.length(); i = i + 2,j++){
			hexByte[j] = (byte)Integer.parseInt(str.substring(i,i+2), 16);
		}
		return hexByte;
	}

	@ReactMethod
	public void checkState() {
		Log.d(LOG_TAG, "checkState");

		BluetoothAdapter adapter = getBluetoothAdapter();
		String state = "off";
		if(!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			state = "unsupported";
		} else if (adapter != null) {
			switch (adapter.getState()) {
				case BluetoothAdapter.STATE_ON:
					state = "on";
					break;
				case BluetoothAdapter.STATE_OFF:
					state = "off";
			}
		}

		WritableMap map = Arguments.createMap();
		map.putString("state", state);
		Log.d(LOG_TAG, "state:" + state);
		sendEvent("BleManagerDidUpdateState", map);
	}


	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static WritableArray bytesToWritableArray(byte[] bytes) {
		WritableArray value = Arguments.createArray();
		for (int i = 0; i < bytes.length; i++)
			value.pushInt((bytes[i] & 0xFF));
		return value;
	}



	//////////////////////////////////////////////////////////////////////////////////////////

    @ReactMethod
	public void initSDK(String appKey,Callback callback)
	{
//		if (!O0OO00.isAuthorized(context, appKey))
//		{
//			Toast.makeText(context, "appKey未授权,无法初始化", Toast.LENGTH_SHORT).show();
//			return;
//		}

		SNBLESDK.init(context);
		SNEventBus.register(this);
//		if (O0OO00.mustAuthorized)
//		{
			isInitialized = SNBLESDK.isInitialized();
//		}
		inhibitionAndroidPAlert(callback);
	}

	private void inhibitionAndroidPAlert(Callback callback)
	{
		if (Build.VERSION.SDK_INT >= 27)
		{
			try
			{
				Class       aClass              = Class.forName("android.content.pm.PackageParser$Package");
				Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
				declaredConstructor.setAccessible(true);
				Class  cls            = Class.forName("android.app.ActivityThread");
				Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
				declaredMethod.setAccessible(true);
				Object activityThread         = declaredMethod.invoke(null);
				Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
				mHiddenApiWarningShown.setAccessible(true);
				mHiddenApiWarningShown.setBoolean(activityThread, true);
			} catch (Throwable ignored)
			{
			    callback.invoke("Throwable");
			}
		}
	}

	@ReactMethod
	public void setDebug(boolean debug)
	{
		BLELog.setIsDebug(debug);
	}

	@ReactMethod
	public void NativeLog(String tag, String msg)
	{
		BLELog.d(tag+msg);
	}

	@ReactMethod
	public void isSDKInitialized(Callback callback)
	{
		if(isInitialized) {
			callback.invoke("not Initialized");
		}
		else
		{
			callback.invoke();
		}
	}


	@ReactMethod
	public void releaseSDK()
	{
		BLELog.d("releaseSDK");
		SNBLESDK.close();
		SNEventBus.unregister(this);
		removeConnectListener();
	}


	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------权限相关-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////
	@ReactMethod
	public void isBluetoothSupportVersion(Callback callback)
	{
		boolean is= SNBLESDK.isBluetoothSupportVersion();
		if(!is) {
			callback.invoke("not BluetoothSupportVersion");
		}
		else
		{
			callback.invoke();
		}
	}

	@ReactMethod
	public void isBluetoothSupportBLE(Callback callback)
	{
		boolean is = SNBLESDK.isBluetoothSupportBLE();
		if(!is) {
			callback.invoke("not BluetoothSupportBLE");
		}
		else
		{
			callback.invoke();
		}
	}

	@ReactMethod
	public void isBluetoothEnable(Callback callback)
	{
		boolean is = SNBLESDK.isBluetoothEnable();
		if(!is) {
			callback.invoke("not BluetoothEnable");
		}
		else
		{
			callback.invoke();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------原生相关API-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////

	public BluetoothAdapter getBluetoothAdapter()
	{
		return SNBLESDK.getBluetoothAdapter();
	}

	public BluetoothManager getBluetoothManager()
	{
		return SNBLESDK.getBluetoothManager();
	}

	public BluetoothGatt getBluetoothGatt()
	{
		return SNBLEHelper.getBluetoothGatt();
	}


	public BluetoothDevice getDevice()
	{
		BluetoothGatt gatt = getBluetoothGatt();
		if (gatt != null)
		{
			return gatt.getDevice();
		} else
		{
			return SNBLESDK.getBluetoothAdapter().getRemoteDevice(DeviceStorage.getDeviceMac());
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------连接状态相关判断-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////
	@ReactMethod
	public void isConnected(Callback callback)
	{
		boolean is = SNBLEHelper.isConnected();
		if(!is) {
			callback.invoke("not Connected");
		}
		else
		{
			callback.invoke();
		}
	}

	@ReactMethod
	public void isConnecting(Callback callback)
	{
		boolean is = SNBLEHelper.isConnecting();
		if(!is) {
			callback.invoke("not Connecting");
		}
		else
		{
			callback.invoke();
		}
	}

	@ReactMethod
	public void isDisconnected(Callback callback)
	{
		boolean is = SNBLEHelper.isDisconnected();
		if(!is) {
			callback.invoke("not Disconnected");
		}
		else
		{
			callback.invoke();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------设备相关信息获取-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////



	@ReactMethod
	public void getDeviceName(Callback callback)
	{
		String name = getDevice().getName();
		if(TextUtils.isEmpty(name)) {
			callback.invoke("");
		}
		else
		{
			callback.invoke(name);
		}
	}

	@ReactMethod
	public void getDeviceMacAddress(Callback callback)
	{
		String addr= getDevice().getAddress();
		if(TextUtils.isEmpty(addr)) {
			callback.invoke("");
		}
		else
		{
			callback.invoke(addr);
		}
	}


	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------GATT蓝牙操作相关-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////
	OnDeviceConnectListener onDeviceConnectListener = new OnDeviceConnectListener()
	{

		@Override
		public void onConnected() {
			WritableMap map = Arguments.createMap();
			map.putInt("type",9991);
			sendEvent("deviceConnectListener", map);
		}

		@Override
		public void onNotifyEnable() {
			WritableMap map = Arguments.createMap();
			map.putInt("type",9990);
			sendEvent("deviceConnectListener", map);
		}

		@Override
		public void onDisconnected() {
			WritableMap map = Arguments.createMap();
			map.putInt("type",9992);
			sendEvent("deviceConnectListener", map);
		}

		@Override
		public void onFailed(int errorType) {
			WritableMap map = Arguments.createMap();
			map.putInt("type",errorType);
			sendEvent("deviceConnectListener", map);
		}
	};

	@ReactMethod
	public void connect(String address,Callback callback)
	{
//		if (!O0OO00.mustAuthorized)
//		{
//			return true;
//		}
//		removeConnectListener(connectListener);
		SNBLEHelper.addConnectListener(onDeviceConnectListener);
		boolean result =  SNBLEHelper.connect(address);
		if(result) {
		    callback.invoke();
		}
		else {
		    callback.invoke("connect failed");
		}

	}

	@ReactMethod
	public void disconnect()
	{
		SNBLEHelper.disconnect();
	}

	private HashMap<byte[], Callback> commandSendStatusListenerHashMap = new HashMap<>();

	@ReactMethod
	public void sendCMDNoResponse(String message)
	{
		sendCMD(message, null);
	}

	@ReactMethod
	public void sendCMDByArrayNoResponse(ReadableArray datas)
	{
		sendCMDByArray(datas, null);
	}

	@ReactMethod
	public void sendCMDByArray(ReadableArray datas, Callback callback)
	{
		if (datas == null) return;
		for(int i=0;i<datas.size();i++)
		{
			String data = datas.getString(i);
			sendCMD(data, callback);
		}

	}

	public void removeListener(Callback callback)
	{
		BLELog.d("removeListener");
		Set<Map.Entry<byte[], Callback>> entries = commandSendStatusListenerHashMap.entrySet();
		for (Map.Entry<byte[], Callback> entry : entries)
		{
			if (entry.getValue() == callback)
			{
				entry.setValue(null);
			}
		}
	}


	public void removeConnectListener()
	{
		BLELog.d("removeConnectListener");
		if(onDeviceConnectListener!=null)
		{
		    SNBLEHelper.removeConnectListener(onDeviceConnectListener);
		    onDeviceConnectListener =null;
		}
	}

	@ReactMethod
	private void sendCMD(String message, Callback callback)
	{
		BLELog.d("==>start sendCMD message "+ message);
		byte [] data = strToHexByteArray(message);
		if (callback != null)
		{
			if (!SNBLESDK.isBluetoothEnable() || !SNBLEHelper.isConnected() || getBluetoothGatt() == null)
			{
				BLELog.d("==>sendCMD failed");
				if (getBluetoothGatt() == null) {
                                	BLELog.d("==>getBluetoothGatt() == null");
                                }
                                if (!SNBLESDK.isBluetoothEnable()) {
                                    BLELog.d("==>!SNBLESDK.isBluetoothEnable()");
                                }
                                if (!SNBLEHelper.isConnected()) {
                                     BLELog.d("==>!SNBLEHelper.isConnected()");
                                }
				callback.invoke("Bluetooth not enable ");
				return;
			}
			byte[] buffer = new byte[20];//防止硬件返回的数据不完整导致app闪退 把所有命令调整为20个字节 没有的补0x00
			System.arraycopy(data, 0, buffer, 0, data.length);//超快速数组复制 这里不用担心性能问题
			//大数据需要使用高优先级连接间隔
			boolean isNeedHighPriority = false;
			//以下3种情况特殊 需要转化下
			//运动大数据
			if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "0703"))
			{
				buffer[2] = (byte) 0xFF;
				isNeedHighPriority = true;
			}
			//睡眠大数据
			if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "0704"))
			{
				buffer[2] = (byte) 0xFE;
				isNeedHighPriority = true;
			}
			//心率大数据
			if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "0707"))
			{
				buffer[2] = (byte) 0xFD;
				isNeedHighPriority = true;
			}
			if (isNeedHighPriority && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			{
				requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
			}
			//if (O0OO00.mustAuthorized)
			{
				commandSendStatusListenerHashMap.put(buffer, callback);
			}
		}
		SNBLEHelper.sendCMD(data);
	}


	private void sendCMD(byte[] data, Callback callback)
	{

		BLELog.d("==>start sendCMD data "+ bytesToHex(data));
		if (callback != null)
		{
			if (!SNBLESDK.isBluetoothEnable() || !SNBLEHelper.isConnected() || getBluetoothGatt() == null)
			{
				BLELog.d("==>sendCMD failed");
				if (getBluetoothGatt() == null) {
                	BLELog.d("==>getBluetoothGatt() == null");
                }
                if (!SNBLESDK.isBluetoothEnable()) {
                    BLELog.d("==>!SNBLESDK.isBluetoothEnable()");
                }
                if (!SNBLEHelper.isConnected()) {
                     BLELog.d("==>!SNBLEHelper.isConnected()");
                }
				callback.invoke("Bluetooth not enable ");
				return;
			}
			byte[] buffer = new byte[20];//防止硬件返回的数据不完整导致app闪退 把所有命令调整为20个字节 没有的补0x00
			System.arraycopy(data, 0, buffer, 0, data.length);//超快速数组复制 这里不用担心性能问题
			//大数据需要使用高优先级连接间隔
			boolean isNeedHighPriority = false;
			//以下3种情况特殊 需要转化下
			//运动大数据
			if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "0703"))
			{
				buffer[2] = (byte) 0xFF;
				isNeedHighPriority = true;
			}
			//睡眠大数据
			if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "0704"))
			{
				buffer[2] = (byte) 0xFE;
				isNeedHighPriority = true;
			}
			//心率大数据
			if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "0707"))
			{
				buffer[2] = (byte) 0xFD;
				isNeedHighPriority = true;
			}
			if (isNeedHighPriority && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			{
				requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
			}
			//if (O0OO00.mustAuthorized)
			{
				commandSendStatusListenerHashMap.put(buffer, callback);
			}
		}
		SNBLEHelper.sendCMD(data);
	}


	private void sendCMD(List<byte[]> datas, Callback callback)
	{
		if (datas == null) return;
		for (byte[] data : datas)
		{
			sendCMD(data, callback);
		}
	}

	@ReactMethod
	public void restartBLEService()
	{
		if (this.context != null)
		{
			if (!SNBLEService.isServiceRunning(context))
			{
				BLELog.d("restartBLEService");
				SNBLEService.startService(context, SNBLEService.class);
			}
		}
	}

	@ReactMethod
	public void isBLEServiceRunning(Callback callback)
	{
		boolean isRunning = SNBLEService.isServiceRunning(context);
		if(!isRunning) {
			callback.invoke("no running");
		}
		else
		{
			callback.invoke();
		}
	}

	@ReactMethod
	public void readRemoteRssi(Callback callback)
	{
		BLELog.d("readRemoteRssi");
		boolean isRead = SNBLEHelper.readRemoteRssi();
		if(!isRead) {
			callback.invoke("no readRemoteRssi");
		}
		else
		{
			callback.invoke();
		}
	}

	public boolean requestConnectionPriority(int connectionPriority)
	{
		BLELog.d("requestConnectionPriority = "+connectionPriority);
		return SNBLEHelper.requestConnectionPriority(connectionPriority);
	}

//	@ReactMethod
//	public void setOnDeviceEventListener(OnDeviceEventListener deviceEventListener)
//	{
//		this.deviceEventListener = deviceEventListener;
//	}
//
//	@ReactMethod
//	public void setOnDeviceDataReceiveListener(OnDeviceDataReceiveListener deviceDataReceiveListener)
//	{
//		this.deviceDataReceiveListener = deviceDataReceiveListener;
//	}

	@ReactMethod
	public void sendFileCMD(List<FilePackage> filePackage, OnWallpaperUploadListener listener)
	{
		SNBLEHelper.sendFileCMD(filePackage, listener);
	}

	@ReactMethod
	public boolean isSupportWallpaper()
	{
		return SNBLEHelper.isSupportWallpaper();
	}

	@ReactMethod
	public boolean setWallpaperNotifyEnable(boolean enable)
	{
		BLELog.d("setWallpaperNotifyEnable");
		return BleHelper.getInstance().setNotifyEnable(BaseUUID.SERVICE, BaseUUID.NOTIFY_WALLPAPER, BaseUUID.DESC, enable);
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventReceived(SNEvent event)
	{
		if (event != null)
		{
			if (event.getCode() == SNBLEEvent.EVENT_BASE_COMMAND)
			{
				BLELog.d("onEventReceived:code=BASE");
			} else
			{
				BLELog.d("onEventReceived:code=" + event.getCode());
			}
		}
//		if (!O0OO00.mustAuthorized)
//		{
//			return;
//		}
		if (event.getCode() == SNBLEEvent.EVENT_BASE_COMMAND)
		{
			byte[] buffer = (byte[]) event.getData();
			if (!commandSendStatusListenerHashMap.isEmpty())
			{
				for (Map.Entry<byte[], Callback> entry : commandSendStatusListenerHashMap.entrySet())
				{
					if (entry.getValue() instanceof OnDeviceCommRawDataListener)
					{
						OnDeviceCommRawDataListener listener = (OnDeviceCommRawDataListener) entry.getValue();
						if (listener != null)
						{
							listener.onRawDataResponse(buffer);
						}
					}

					if (buffer.length >= 4)
					{
						byte[] bytes = entry.getKey();
						if (bytes.length >= 3)
						{
							//头部相同
							if (buffer[0] == bytes[0] && buffer[1] == bytes[1] && buffer[2] == bytes[2])
							{


								Callback listener = commandSendStatusListenerHashMap.get(bytes);

									//实时运动数据 直接返回成功
									if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "0701"))
									{
										//((OnDeviceCommStatusListener) listener).onResponse(true);
										listener.invoke("","true");
										return;
									}
									//获取设备基本信息0 直接返回成功
									if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "0102"))
									{
										//((OnDeviceCommStatusListener) listener).onResponse(true);
										listener.invoke("","true");
										return;
									}
									//大数据需要使用高优先级连接间隔,传输完成后 恢复
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && (
											SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "07FF") ||
													SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "07FE") ||
													SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "07FD")))
									{
										requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_BALANCED);
									}
									if (buffer[2] == 0x0B)
									{
										break;
									}
									listener.invoke("",buffer[3] == 0x01?"true":"false");
									//((OnDeviceCommStatusListener) listener).onResponse(buffer[3] == 0x01);
									break;

							} else if (buffer[0] == bytes[0] && buffer[1] == bytes[1])
							{
								Callback listener = commandSendStatusListenerHashMap.get(bytes);

									if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "07F9"))
									{
									    listener.invoke("",buffer[3] == 0x01?"true":"false");
										return;
									}

							}
						}
					}
				}
			}
		}

			@SNBLEEvent.DeviceEvent
			int code = event.getCode();
			WritableMap map = Arguments.createMap();
			WritableArray writableArray = Arguments.createArray();
			switch (code)
			{
				case SNBLEEvent.EVENT_DEVICE_CAMERA_TAKE_PHOTO:
				case SNBLEEvent.EVENT_DEVICE_CALL_END_CALL:
				case SNBLEEvent.EVENT_DEVICE_CALL_MUTE:
				case SNBLEEvent.EVENT_DEVICE_FIND_PHONE:
				case SNBLEEvent.EVENT_DEVICE_MUSIC_PLAY_OR_PAUSE:
				case SNBLEEvent.EVENT_DEVICE_MUSIC_PREVIOUS:
				case SNBLEEvent.EVENT_DEVICE_MUSIC_NEXT:
					//deviceEventListener.onDeviceEventChanged(code);

					map.putInt("code",code);
					sendEvent("deviceEventListener", map);
					break;
				case SNBLEEvent.EVENT_DATA_HEALTH_HEART_RATE:
				case SNBLEEvent.EVENT_DATA_HEALTH_BLOOD_OXYGEN:
				{
					int params = (Integer) event.getData();
					map.putInt("code",code);
					map.putInt("params",params);
					sendEvent("deviceDataReceiveListener", map);

					//deviceDataReceiveListener.onDeviceDataChanged(code, params);
				}
				break;
				case SNBLEEvent.EVENT_DATA_HEALTH_BLOOD_PRESSURE:
				{
					int[] params = (int[]) event.getData();
					map.putInt("code",code);
					writableArray.pushInt( params[0]);
					writableArray.pushInt( params[1]);
					map.putArray("params",writableArray);
					sendEvent("deviceDataReceiveListener", map);
					//deviceDataReceiveListener.onDeviceDataChanged(code, params[0], params[1]);
				}
				break;
				case SNBLEEvent.EVENT_HISTORY_SPORT_DATA:
				case SNBLEEvent.EVENT_HISTORY_SLEEP_DATA:
				case SNBLEEvent.EVENT_HISTORY_HEART_RATE_DATA:
				case SNBLEEvent.EVENT_HISTORY_SPORT_MODE_DATA:
				case SNBLEEvent.EVENT_DATA_DEVICE_INFO:
				{
					Object params = event.getData();
					String jsonString= JsonTools.toJson(params);
					map.putInt("code",code);
					map.putString("params",jsonString);
					sendEvent("deviceDataReceiveListener", map);
					//deviceDataReceiveListener.onDeviceDataChanged(code, params);
				}
				break;
				case SNBLEEvent.EVENT_DATA_REAL_TIME_SPORT_DATA:
				{
					int[] params = (int[]) event.getData();
					map.putInt("code",code);
					for(int i=0 ;i<params.length;i++)
					{
						writableArray.pushInt(params[i]);
					}
					map.putArray("params",writableArray);
					sendEvent("deviceDataReceiveListener", map);

					//deviceDataReceiveListener.onDeviceDataChanged(code, params[0], params[1], params[2]);
				}
				break;
			}

	}


////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////BLE Scanner////////////////////////////////////////


	@ReactMethod
	public void isScanning(Callback callback)
	{
		BLELog.d("isScanning");
		boolean isScan = SNBLEScanner.isScanning();
		if(isScan) {
			callback.invoke("is Scanning");
		}
		else
		{
			callback.invoke();
		}
	}


	@ReactMethod
	public void startScan() {
		BLELog.d("startScan");
		SNBLEScanner.startScan(new OnDeviceLeScanListener() {
			@Override
			public void onScanStart() {
				WritableMap map = Arguments.createMap();
				map.putString("status","scanStart");
				sendEvent("deviceScanListener", map);
			}

			@Override
			public void onScanning(SNBLEDevice device) {
				WritableMap map = Arguments.createMap();
				map.putString("status","scanning");
				map.putString("deviceName",device.mDeviceName);
				map.putString("deviceAddr",device.mDeviceAddress);
				map.putInt("mRssi",device.mRssi);
				map.putArray("scanRecord",bytesToWritableArray(device.mScanRecord));
				sendEvent("deviceScanListener", map);
			}

			@Override
			public void onScanStop() {
				WritableMap map = Arguments.createMap();
				map.putString("status","scanStop");
				sendEvent("deviceScanListener", map);
			}

			@Override
			public void onScanTimeout() {
				WritableMap map = Arguments.createMap();
				map.putString("status","scanTimeout");
				sendEvent("deviceScanListener", map);
			}
		});

	}

	@ReactMethod
	public void scanDestroy() {
		BLELog.d("scanDestroy");
		SNBLEScanner.destroy();
	}

	@ReactMethod
	public void stopScan() {
		BLELog.d("stopScan");
		SNBLEScanner.stopScan();
	}



////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////cmd data///////////////////////////////////////////

	/**
	 * 3.1  请求设备重启  (重启/重置/清除数据)
	 *
	 * @return
	 */

	@ReactMethod
	public void setDeviceRestart(Callback callback)
	{
		BLELog.d("setDeviceRestart");
		sendCMD(SNCMD.getInstance().setDeviceRestart(),callback);
	}


	/**
	 * 3.2 请求设备进入空中升级模式
	 *
	 * @return
	 */
	@ReactMethod
	public void setDeviceOTAMode(Callback callback)
	{
		BLELog.d("setDeviceOTAMode");
		sendCMD(SNCMD.getInstance().setDeviceOTAMode(),callback);
	}

	/**
	 * 4.16  设置天气
	 *
	 * @param weather 天气类型(见附表 天气表)
	 * @param curTem  当前温度
	 * @param maxTem  最大温度
	 * @param minTem  最小温度
	 * @return
	 */
	@ReactMethod
	public void setDeviceWeatherInfo(int weather, int curTem, int maxTem, int minTem,Callback callback)
	{
		BLELog.d("setDeviceWeatherInfo");
		sendCMD(SNCMD.getInstance().setDeviceWeatherInfo( weather,  curTem,  maxTem,  minTem),callback);
	}

	/**
	 * 4.16  设置天气
	 *
	 * @param weather 天气类型(见附表 天气表)
	 * @param curTem  当前温度
	 * @param maxTem  最大温度
	 * @param minTem  最小温度
	 * @param dayOffset  0x00今天, 0x01明天, 0x02后天.
	 * @return
	 */
	@ReactMethod
	public void setDeviceWeatherInfo(int weather, int curTem, int maxTem, int minTem, int dayOffset,Callback callback)
	{
		BLELog.d("setDeviceWeatherInfo");
		sendCMD(SNCMD.getInstance().setDeviceWeatherInfo( weather,  curTem,  maxTem,  minTem,  dayOffset),callback);
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------5.Command 0x02 设置命令 -------------------------
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 5.1  设置时间
	 *
	 * @param time 时间戳
	 * @return
	 */
	@ReactMethod
	public void setDeviceTime(long time,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setDeviceTime(time),callback);
	}
	/**
	 * 5.2设置基本信息
	 *
	 * @param gender          1男, 2女
	 * @param age             年龄
	 * @param height          单位CM
	 * @param weight          单位kg
	 * @param handedness      0：左手  1：右手
	 * @param timeUnit        0：24小时制, 1：12小时制
	 * @param distanceUnit    0：公制  1：英制
	 * @param temperatureUnit 0：摄氏度  1：华摄氏度
	 * @param clientLanguage  0：中文, 1：英文
	 * @param targetSteps     目标步数
	 * @return
	 */
	@ReactMethod
	public void setDeviceUserInfo(int gender, int age, float height, float weight, int handedness, int timeUnit, int distanceUnit, int temperatureUnit, int clientLanguage, int targetSteps,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setDeviceUserInfo( gender, age, height, weight, handedness, timeUnit, distanceUnit, temperatureUnit, clientLanguage, targetSteps),callback);
	}

	/**
	 * 5.4 设置 闹钟提醒
	 *
	 * @param enable      true开启/false关闭
	 * @param id          序号(从0开始)
	 * @param repeatWeeks 重复周期   则周日周一周二...周六, true时为 重复, false为不重复
	 * @param hour        小时
	 * @param minute      分钟
	 * @param tag         标签 默认0
	 * @return
	 */
	@ReactMethod
	public void setAlarmReminderInfo(boolean enable, int id, boolean[] repeatWeeks, int hour, int minute, int tag,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setAlarmReminderInfo( enable, id, repeatWeeks, hour, minute, tag),callback);
	}
	/**
	 * 5.5 设置日程提醒
	 *
	 * @param date 日程日期, 格式是: yyyy-MM-dd HH:mm 切记不带秒
	 * @param tag  标签(默认0)
	 * @return
	 */
	@ReactMethod
	public void setScheduleReminderInfo(boolean enable, int id, String date, int tag,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setScheduleReminderInfo( enable,  id, date,  tag),callback);
	}
	/**
	 * 5.6喝水提醒
	 *
	 * @param enable       true开启/false关闭
	 * @param repeatWeeks  重复周期   则周日周一周二...周六, true时为 重复, false为不重复
	 * @param beginHour    开始的小时
	 * @param beginMinute  开始的分钟
	 * @param endHour      结束的小时
	 * @param endMinute    结束的分钟
	 * @param intervalTime 时间间隔 (分钟)
	 * @return
	 */
	@ReactMethod
	public void setDrinkReminderInfo(boolean enable, boolean[] repeatWeeks, int beginHour, int beginMinute, int endHour, int endMinute, int intervalTime,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setDrinkReminderInfo( enable,repeatWeeks, beginHour, beginMinute, endHour, endMinute, intervalTime),callback);
	}
	/**
	 * 5.7久坐提醒
	 *
	 * @param enable       true开启/false关闭
	 * @param repeatWeeks  重复周期   则周日周一周二...周六, true时为 重复, false为不重复
	 * @param beginHour    开始的小时
	 * @param beginMinute  开始的分钟
	 * @param endHour      结束的小时
	 * @param endMinute    结束的分钟
	 * @param intervalTime 时间间隔 (分钟)
	 * @return
	 */
	@ReactMethod
	public void setSedentaryReminderInfo(boolean enable, boolean[] repeatWeeks, int beginHour, int beginMinute, int endHour, int endMinute, int intervalTime,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setSedentaryReminderInfo( enable, repeatWeeks, beginHour, beginMinute, endHour, endMinute, intervalTime),callback);
	}

	/**
	 * 5.8 定时体检
	 * 略。
	 */

	/**
	 * 5.9 提醒设置 ，适配于 S IOS  系统
	 * 略。
	 */

	/**
	 * 5.10 设备其他功能设置
	 * 抬腕亮屏、低电量提醒、防丢开关、心率自动体检测开关、翻腕切屏
	 *
	 * @param lowBattery                       true开启/false关闭
	 * @param lightScreen                      true开启/false关闭
	 * @param antiLost                         true开启/false关闭
	 * @param heartRateAutoDetect              true开启/false关闭
	 * @param cutScreen                        true开启/false关闭
	 * @param heartRateAutoDetectIntervalType 心率自动检测间隔时间设置 , 参数传0: 保留,1: 15分钟, 2:30分钟 3:60分钟
	 * @return
	 */
	@ReactMethod
	public void setDeviceConfigInfo(boolean lowBattery, boolean lightScreen, boolean antiLost, boolean heartRateAutoDetect, boolean cutScreen, int heartRateAutoDetectIntervalType,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setDeviceConfigInfo( lowBattery, lightScreen, antiLost, heartRateAutoDetect, cutScreen, heartRateAutoDetectIntervalType),callback);
	}
	/**
	 * 5.11 设置 夜间勿扰模式
	 *
	 * @param enable      true开启/false关闭
	 * @param beginHour   开始时
	 * @param beginMinute 开始分
	 * @param endHour     结束时
	 * @param endMinute   结束分
	 * @return
	 */
	@ReactMethod
	public void setDeviceNightModeInfo(boolean enable, int beginHour, int beginMinute, int endHour, int endMinute,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setDeviceNightModeInfo(enable, beginHour, beginMinute, endHour, endMinute),callback);
	}
	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------6. 绑定命令 略-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------7.提醒命令-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 7.1 发送来电提醒
	 *
	 * @param title       标题
	 * @param phoneNumber 号码(内容)
	 * @return
	 */
	@ReactMethod
	public void setCallMessage(String title, String phoneNumber,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setCallMessage( title, phoneNumber),callback);
	}
	/**
	 * 7.2发送短信提醒
	 *
	 * @param title   标题
	 * @param content 内容
	 */
	@ReactMethod
	public void setSMSMessage(String title, String content,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setSMSMessage(title, content),callback);
	}
	/**
	 * 7.3 App消息提醒
	 *
	 * @param title   标题
	 * @param content 内容
	 * @return
	 */
	@ReactMethod
	public void setAppMessage(String title, String content,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setAppMessage(title, content),callback);
	}
//
//    /**
//     * 7.4  设置日程提醒标签内容
//     *
//     * @param id 最大数目为
//     * @param tag 标签内容
//     * @return
//     */
//    byte[] setScheduleReminderTagInfo(int id, String tag);


	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------8. command 0x05 APP 端控制命令-------------------
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 8.1 打开或关闭心率开关
	 *
	 * @param enable  true开启/false关闭
	 * @return
	 */
	@ReactMethod
	public void setHeartRateStatus(boolean enable,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setHeartRateStatus( enable),callback);
	}
	/**
	 * 8.2 相机开关
	 *
	 * @param enable  true开启/false关闭
	 * @return
	 */
	@ReactMethod
	public void setCameraModeStatus(boolean enable,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setCameraModeStatus(enable),callback);
	}
	/**
	 * 8.3 找手环
	 *
	 * @param enable  true开启/false关闭
	 * @return
	 */
	@ReactMethod
	public void setFindDeviceStatus(boolean enable,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setFindDeviceStatus( enable),callback);
	}
	/**
	 * 8.4 血氧开关
	 *
	 * @param enable  true开启/false关闭
	 * @return
	 */
	@ReactMethod
	public void setBloodOxygenStatus(boolean enable,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setBloodOxygenStatus(enable),callback);
	}
	/**
	 * 8.5 血压开关
	 *
	 * @param enable  true开启/false关闭
	 * @return
	 */
	@ReactMethod
	public void setBloodPressureStatus(boolean enable,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setBloodPressureStatus(enable),callback);
	}

	/**
	 * 8.6 来电处理状态 ， 适配于Android系统
	 *
	 * @param status  0:保留，默认状态。
	 *                1:来电，手机已经接通，开始通话
	 *                2 来电，手机已经挂断。
	 * @return
	 */
	@ReactMethod
	public void setCallStatus(int status,Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().setCallStatus(status),callback);
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------9. command 0x06 固件设备控制命令------------------
	////////////////////////////////////////////////////////////////////////////////////////

	//属于接收命令 不在此处解析

	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------10. command 7 0x07 健康数据命令（运动，心率和睡眠）-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 10.1 取得实时运动数据
	 *
	 * @return
	 */
	@ReactMethod
	public void getRealTimeSportData(Callback callback)
	{
		BLELog.d("setDeviceTime");
		sendCMD(SNCMD.getInstance().getRealTimeSportData(),callback);
	}

//    /**
//     * 10.2  读取设备的实时心率数据 血氧 血压等
//     *
//     * @return
//     */
//    byte[] getRealTimeHeartRateData();


	/**
	 * 10.3  取得 计步距离卡路里历史数据
	 *
	 * @return
	 */
	@ReactMethod
	public void getHistorySportData(Callback callback)
	{
		BLELog.d("getHistorySportData");
		sendCMD(SNCMD.getInstance().getHistorySportData(),callback);
	}
	/**
	 * 10.4 取得 睡眠历史数据
	 *
	 * @return
	 */
	@ReactMethod
	public void getHistorySleepData(Callback callback)
	{
		BLELog.d("getHistorySleepData");
		sendCMD(SNCMD.getInstance().getHistorySleepData(),callback);
	}
	/**
	 * 10.5 取得 心率历史数据
	 *
	 * @return
	 */
	@ReactMethod
	public void getHistoryHeartRateData(Callback callback)
	{
		BLELog.d("getHistoryHeartRateData");
		sendCMD(SNCMD.getInstance().getHistoryHeartRateData(),callback);
	}



	/**
	 * 5.17 app设置设备是否进入高速传输模式
	 * (用完记得关闭)
	 * @param enable
	 */

	@ReactMethod
	public void  setHighSpeedTransportStatus(boolean enable,Callback callback)
	{
		BLELog.d("setHighSpeedTransportStatus");
		sendCMD(SNCMD.getInstance(). setHighSpeedTransportStatus( enable),callback);
	}
//	List<FilePackage> createWallpaperPackage(Bitmap src, int width, int height);
//
//	List<FilePackage> createFilePackage(byte[] bytes);


	/**
	 * 4.20 获取设备是否支持屏保及设备显示分辨率
	 */
	@ReactMethod
	public void getWallpaperScreenInfo(Callback callback)
	{
		BLELog.d("getWallpaperScreenInfo");
		sendCMD(SNCMD.getInstance().getWallpaperScreenInfo(),callback);
	}

	/**
	 * 4.21 获取设备是否支持屏保的文字种类,以及对应的分辨率
	 */
	@ReactMethod
	public void getWallpaperFontInfo(Callback callback)
	{
		BLELog.d("getWallpaperFontInfo");
		sendCMD(SNCMD.getInstance().getWallpaperFontInfo(),callback);
	}
	/**
	 * 5.18   APP  设置设备 是否 开启屏保 功能
	 */

	@ReactMethod
	public void  setWallpaperEnable(boolean enable,Callback callback)
	{
		BLELog.d("setWallpaperEnable");
		sendCMD(SNCMD.getInstance(). setWallpaperEnable( enable),callback);
	}
	/**
	 * 5.19 APP  设置设备屏保 显示时间的 具体 信息
	 * @param enable
	 * @param isHorizontal
	 * @param fontWidth
	 * @param fontHeight
	 * @param colorRgb888
	 * @param x
	 * @param y
	 * @return
	 * Ox00:保留
	 * 0x01:设置成功
	 * 0x02:显示方式不支持
	 * 0x03:需要显示的字体分辨率不支持
	 * 0x04:需要显示的字体颜色不支持
	 * 0x05:X 轴，Y 轴起始坐标点异常
	 * 0x06:X 轴，Y 轴起始坐标点正常，根据计算，已越界
	 * 0x07:与其他屏保效果块重叠
	 */
	@ReactMethod
	public void  setWallpaperTimeInfo(boolean enable, boolean isHorizontal, int fontWidth, int fontHeight, int colorRgb888, int x, int y,Callback callback)
	{
		BLELog.d("setWallpaperTimeInfo");
		sendCMD(SNCMD.getInstance(). setWallpaperTimeInfo( enable, isHorizontal, fontWidth, fontHeight, colorRgb888, x, y),callback);
	}
	/**
	 * 5.20 APP  设置设备屏保 显示 记步 的 具体 信息
	 */
	@ReactMethod
	public void  setWallpaperStepInfo(boolean enable, boolean isHorizontal, int fontWidth, int fontHeight, int colorRgb888, int x, int y,Callback callback)
	{
		BLELog.d("setWallpaperStepInfo");
		sendCMD(SNCMD.getInstance().setWallpaperStepInfo( enable, isHorizontal, fontWidth, fontHeight, colorRgb888, x, y),callback);
	}

	/**
	 * 5.21 APP 设置设备实时运动数据的上传方式
	 */
	@ReactMethod
	public void  setSyncRealTimeSportDataRealTimeCallback(boolean realTime,Callback callback)
	{
		BLELog.d("setSyncRealTimeSportDataRealTimeCallback");
		sendCMD(SNCMD.getInstance(). setSyncRealTimeSportDataRealTimeCallback( realTime),callback);
	}
	/**
	 * 10.9 运动模式大数据
	 * @return
	 */
	@ReactMethod
	public void  getHistorySportModelData(Callback callback)
	{
		BLELog.d("getHistorySportModelData");
		sendCMD(SNCMD.getInstance(). getHistorySportModelData(),callback);
	}

	@ReactMethod
	public void  toast(String msg)
	{
		Toast.makeText(this.context,msg,Toast.LENGTH_SHORT);

	}

}
