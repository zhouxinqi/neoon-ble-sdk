package com.neoon.rn;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SNBLEManagerPackage implements ReactPackage {

	public SNBLEManagerPackage() {}

	@Override
	public List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
		List<NativeModule> modules = new ArrayList<>();

		modules.add(new SNRNBleManager(reactApplicationContext));
		return  modules;
	}

	public List<Class<? extends JavaScriptModule>> createJSModules() {
		return new ArrayList<>();
	}

	@Override
	public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
		return Collections.emptyList();
	}
}
