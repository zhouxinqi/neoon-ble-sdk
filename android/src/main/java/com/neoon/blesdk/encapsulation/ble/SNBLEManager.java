package com.neoon.blesdk.encapsulation.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.neoon.blesdk.core.ble.BaseUUID;
import com.neoon.blesdk.core.ble.BleHelper;
import com.neoon.blesdk.core.entity.FilePackage;
import com.neoon.blesdk.core.interfaces.OnWallpaperUploadListener;
import com.neoon.blesdk.core.utils.BLELog;
import com.neoon.blesdk.encapsulation.cmd.SNCMD;
import com.neoon.blesdk.encapsulation.entity.SNBLEEvent;
import com.neoon.blesdk.encapsulation.service.SNBLEService;
import com.neoon.blesdk.encapsulation.storage.DeviceStorage;
import com.neoon.blesdk.interfaces.OnDeviceCommBaseListener;
import com.neoon.blesdk.interfaces.OnDeviceCommRawDataListener;
import com.neoon.blesdk.interfaces.OnDeviceCommStatusListener;
import com.neoon.blesdk.interfaces.OnDeviceConnectListener;
import com.neoon.blesdk.interfaces.OnDeviceDataReceiveListener;
import com.neoon.blesdk.interfaces.OnDeviceEventListener;
import com.neoon.blesdk.util.AppKey;
import com.neoon.blesdk.util.eventbus.SNEvent;
import com.neoon.blesdk.util.eventbus.SNEventBus;

//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 作者:东芝(2018/7/5).
 * 功能:蓝牙操作主要管理器
 */

public class SNBLEManager implements ISNBLEManager
{

	private OnDeviceEventListener       deviceEventListener;
	private OnDeviceDataReceiveListener deviceDataReceiveListener;
	private boolean                     isInitialized;
	private AppKey                      O0OO00 = new AppKey();

	private SNBLEManager()
	{
	}

	private static class SingletonHolder
	{
		private final static SNBLEManager instance = new SNBLEManager();
	}

	public static SNBLEManager getInstance()
	{
		return SingletonHolder.instance;
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------sdk初始化-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////


	@Override
	public void initSDK(Context context, String appKey)
	{
		if (!O0OO00.isAuthorized(context, appKey))
		{
			Toast.makeText(context, "appKey未授权,无法初始化", Toast.LENGTH_SHORT).show();
			return;
		}

		SNBLESDK.init(context);
		SNEventBus.register(this);
		if (O0OO00.mustAuthorized)
		{
			isInitialized = SNBLESDK.isInitialized();
		}
		inhibitionAndroidPAlert();
	}

	private void inhibitionAndroidPAlert()
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
				Field  mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
				mHiddenApiWarningShown.setAccessible(true);
				mHiddenApiWarningShown.setBoolean(activityThread, true);
			} catch (Throwable ignored)
			{
			}
		}
	}

	@Override
	public void setDebug(boolean debug)
	{
		BLELog.setIsDebug(debug);
	}

	@Override
	public boolean isSDKInitialized()
	{
		return isInitialized;
	}


	@Override
	public void releaseSDK()
	{
		BLELog.d("releaseSDK");
		SNBLESDK.close();
		SNEventBus.unregister(this);
	}


	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------权限相关-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isBluetoothSupportVersion()
	{
		return SNBLESDK.isBluetoothSupportVersion();
	}

	@Override
	public boolean isBluetoothSupportBLE()
	{
		return SNBLESDK.isBluetoothSupportBLE();
	}

	@Override
	public boolean isBluetoothEnable()
	{
		return SNBLESDK.isBluetoothEnable();
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------原生相关API-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public BluetoothAdapter getBluetoothAdapter()
	{
		return SNBLESDK.getBluetoothAdapter();
	}

	@Override
	public BluetoothManager getBluetoothManager()
	{
		return SNBLESDK.getBluetoothManager();
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------连接状态相关判断-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean isConnected()
	{
		return SNBLEHelper.isConnected();
	}

	@Override
	public boolean isConnecting()
	{
		return SNBLEHelper.isConnecting();
	}

	@Override
	public boolean isDisconnected()
	{
		return SNBLEHelper.isDisconnected();
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------设备相关信息获取-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public BluetoothGatt getBluetoothGatt()
	{
		return SNBLEHelper.getBluetoothGatt();
	}

	@Override
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

	@Override
	public String getDeviceName()
	{
		String name = getDevice().getName();
		return TextUtils.isEmpty(name) ? "" : name;
	}

	@Override
	public String getDeviceMacAddress()
	{
		return getDevice().getAddress();
	}


	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------GATT蓝牙操作相关-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean connect(String address, OnDeviceConnectListener connectListener)
	{
		if (!O0OO00.mustAuthorized)
		{
			return true;
		}
		removeConnectListener(connectListener);
		SNBLEHelper.addConnectListener(connectListener);
		return SNBLEHelper.connect(address);
	}

	@Override
	public void disconnect()
	{
		SNBLEHelper.disconnect();
	}

	private HashMap<byte[], OnDeviceCommBaseListener> commandSendStatusListenerHashMap = new HashMap<>();

	@Override
	public void sendCMD(byte[] data)
	{
		sendCMD(data, null);
	}

	@Override
	public void sendCMD(List<byte[]> datas)
	{
		sendCMD(datas, null);
	}

	@Override
	public void sendCMD(List<byte[]> datas, OnDeviceCommBaseListener commandSendStatusListener)
	{
		if (datas == null) return;
		for (byte[] data : datas)
		{
			sendCMD(data, commandSendStatusListener);
		}
	}

	@Override
	public void removeListener(OnDeviceCommBaseListener commandSendStatusListener)
	{
		BLELog.d("removeListener");
		Set<Map.Entry<byte[], OnDeviceCommBaseListener>> entries = commandSendStatusListenerHashMap.entrySet();
		for (Map.Entry<byte[], OnDeviceCommBaseListener> entry : entries)
		{
			if (entry.getValue() == commandSendStatusListener)
			{
				entry.setValue(null);
			}
		}
	}

	@Override
	public void removeConnectListener(OnDeviceConnectListener onDeviceConnectListener)
	{
		BLELog.d("removeConnectListener");
		SNBLEHelper.removeConnectListener(onDeviceConnectListener);
	}

	@Override
	public void sendCMD(byte[] data, OnDeviceCommBaseListener commandSendStatusListener)
	{

		if (commandSendStatusListener != null)
		{
			if (!isBluetoothEnable() || !isConnected() || getBluetoothGatt() == null)
			{
				if (commandSendStatusListener instanceof OnDeviceCommStatusListener)
				{
					((OnDeviceCommStatusListener) commandSendStatusListener).onResponse(false);
				}
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
			if (O0OO00.mustAuthorized)
			{
				commandSendStatusListenerHashMap.put(buffer, commandSendStatusListener);
			}
		}
		SNBLEHelper.sendCMD(data);
	}

	@Override
	public void restartBLEService(Context context)
	{
		if (context != null)
		{
			context = context.getApplicationContext();
			if (!isBLEServiceRunning(context))
			{
				BLELog.d("restartBLEService");
				SNBLEService.startService(context, SNBLEService.class);
			}
		}
	}

	@Override
	public boolean isBLEServiceRunning(Context context)
	{
		return SNBLEService.isServiceRunning(context);
	}

	@Override
	public boolean readRemoteRssi()
	{
		BLELog.d("readRemoteRssi");
		return SNBLEHelper.readRemoteRssi();
	}

	@Override
	public boolean requestConnectionPriority(int connectionPriority)
	{
		BLELog.d("requestConnectionPriority = "+connectionPriority);
		return SNBLEHelper.requestConnectionPriority(connectionPriority);
	}

	@Override
	public void setOnDeviceEventListener(OnDeviceEventListener deviceEventListener)
	{
		this.deviceEventListener = deviceEventListener;
	}

	@Override
	public void setOnDeviceDataReceiveListener(OnDeviceDataReceiveListener deviceDataReceiveListener)
	{
		this.deviceDataReceiveListener = deviceDataReceiveListener;
	}

	@Override
	public void sendFileCMD(List<FilePackage> filePackage, OnWallpaperUploadListener listener)
	{
		SNBLEHelper.sendFileCMD(filePackage, listener);
	}

	@Override
	public boolean isSupportWallpaper()
	{
		return SNBLEHelper.isSupportWallpaper();
	}

	@Override
	public boolean setWallpaperNotifyEnable(boolean enable)
	{
		BLELog.d("setWallpaperNotifyEnable");
		return BleHelper.getInstance().setNotifyEnable(BaseUUID.SERVICE, BaseUUID.NOTIFY_WALLPAPER, BaseUUID.DESC, enable);
	}


	////////////////////////////////////////////////////////////////////////////////////////
	//--------------------------------------接收时间回调处理相关-----------------------------------
	////////////////////////////////////////////////////////////////////////////////////////

	//@Subscribe(threadMode = ThreadMode.MAIN)
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
		if (!O0OO00.mustAuthorized)
		{
			return;
		}
		if (event.getCode() == SNBLEEvent.EVENT_BASE_COMMAND)
		{
			byte[] buffer = (byte[]) event.getData();
			if (!commandSendStatusListenerHashMap.isEmpty())
			{
				for (Map.Entry<byte[], OnDeviceCommBaseListener> entry : commandSendStatusListenerHashMap.entrySet())
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


								OnDeviceCommBaseListener listener = commandSendStatusListenerHashMap.get(bytes);
								if (listener instanceof OnDeviceCommStatusListener)
								{
									//实时运动数据 直接返回成功
									if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "0701"))
									{
										((OnDeviceCommStatusListener) listener).onResponse(true);
										return;
									}
									//获取设备基本信息0 直接返回成功
									if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "0102"))
									{
										((OnDeviceCommStatusListener) listener).onResponse(true);
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
									((OnDeviceCommStatusListener) listener).onResponse(buffer[3] == 0x01);
									break;
								}
							} else if (buffer[0] == bytes[0] && buffer[1] == bytes[1])
							{
								OnDeviceCommBaseListener listener = commandSendStatusListenerHashMap.get(bytes);
								if (listener instanceof OnDeviceCommStatusListener)
								{
									if (SNBLEHelper.startWith(buffer, SNCMD.HEAD_STR + "07F9"))
									{
										((OnDeviceCommStatusListener) listener).onResponse(buffer[3] == 0x01);
										return;
									}
								}
							}
						}
					}
				}
			}
		}
		if (deviceEventListener != null)
		{
			@SNBLEEvent.DeviceEvent
			int code = event.getCode();
			switch (code)
			{
				case SNBLEEvent.EVENT_DEVICE_CAMERA_TAKE_PHOTO:
				case SNBLEEvent.EVENT_DEVICE_CALL_END_CALL:
				case SNBLEEvent.EVENT_DEVICE_CALL_MUTE:
				case SNBLEEvent.EVENT_DEVICE_FIND_PHONE:
				case SNBLEEvent.EVENT_DEVICE_MUSIC_PLAY_OR_PAUSE:
				case SNBLEEvent.EVENT_DEVICE_MUSIC_PREVIOUS:
				case SNBLEEvent.EVENT_DEVICE_MUSIC_NEXT:
					deviceEventListener.onDeviceEventChanged(code);
					break;
			}
		}
		if (deviceDataReceiveListener != null)
		{
			@SNBLEEvent.DeviceData
			int code = event.getCode();
			switch (code)
			{
				case SNBLEEvent.EVENT_DATA_HEALTH_HEART_RATE:
				case SNBLEEvent.EVENT_DATA_HEALTH_BLOOD_OXYGEN:
				{
					int params = (Integer) event.getData();
					deviceDataReceiveListener.onDeviceDataChanged(code, params);
				}
				break;
				case SNBLEEvent.EVENT_DATA_HEALTH_BLOOD_PRESSURE:
				{
					int[] params = (int[]) event.getData();
					deviceDataReceiveListener.onDeviceDataChanged(code, params[0], params[1]);
				}
				break;
				case SNBLEEvent.EVENT_HISTORY_SPORT_DATA:
				case SNBLEEvent.EVENT_HISTORY_SLEEP_DATA:
				case SNBLEEvent.EVENT_HISTORY_HEART_RATE_DATA:
				case SNBLEEvent.EVENT_HISTORY_SPORT_MODE_DATA:
				case SNBLEEvent.EVENT_DATA_DEVICE_INFO:
				{
					Object params = event.getData();
					deviceDataReceiveListener.onDeviceDataChanged(code, params);
				}
				break;
				case SNBLEEvent.EVENT_DATA_REAL_TIME_SPORT_DATA:
				{
					int[] params = (int[]) event.getData();
					deviceDataReceiveListener.onDeviceDataChanged(code, params[0], params[1], params[2]);
				}
				break;
			}
		}
	}

}
