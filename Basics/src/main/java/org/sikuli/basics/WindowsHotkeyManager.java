/*
 * Copyright 2010-2013, Sikuli.org
 * Released under the MIT License.
 *
 * modified RaiMan 2013
 */
package org.sikuli.basics;

import com.melloware.jintellitype.JIntellitype;
import java.util.*;

public class WindowsHotkeyManager extends HotkeyManager {
  private Map<Integer, HotkeyData> _idCallbackMap = new HashMap<Integer, HotkeyData>();
  private int _gHotkeyId = 1;

  class HotkeyData {
    int key, modifiers;
    HotkeyListener listener;
    public HotkeyData(int key_, int mod_, HotkeyListener l_) {
      key = key_;
      modifiers = mod_;
      listener = l_;
    }
  }

  class JIntellitypeHandler implements com.melloware.jintellitype.HotkeyListener {
    @Override
    public void onHotKey(int id) {
      Debug.log(4, "Hotkey pressed");
      HotkeyData data = _idCallbackMap.get(id);
      HotkeyEvent e = new HotkeyEvent(data.key, data.modifiers);
      data.listener.invokeHotkeyPressed(e);
    }
  }

  @Override
  public boolean _addHotkey(int keyCode, int modifiers, HotkeyListener listener) {
    JIntellitype itype = JIntellitype.getInstance();
    if (_gHotkeyId == 1) {
      itype.addHotKeyListener(new JIntellitypeHandler());
    }
    _removeHotkey(keyCode, modifiers);
    int id = _gHotkeyId++;
    HotkeyData data = new HotkeyData(keyCode, modifiers, listener);
    _idCallbackMap.put(id, data);
    itype.registerHotKey(id, translateMod(modifiers), keyCode);
    return true;
  }

  @Override
  public boolean _removeHotkey(int keyCode, int modifiers) {
    for (Map.Entry<Integer, HotkeyData> entry : _idCallbackMap.entrySet()) {
      HotkeyData data = entry.getValue();
      if (data.key == keyCode && data.modifiers == modifiers) {
        JIntellitype itype = JIntellitype.getInstance();
        int id = entry.getKey();
        itype.unregisterHotKey(id);
        _idCallbackMap.remove(id);
        return true;
      }
    }
    return false;
  }

  @Override
  public void cleanUp() {
    JIntellitype itype = JIntellitype.getInstance();
    for (Map.Entry<Integer, HotkeyData> entry : _idCallbackMap.entrySet()) {
      int id = entry.getKey();
      itype.unregisterHotKey(id);
    }
    _gHotkeyId = 1;
    _idCallbackMap.clear();
    itype.cleanUp();
  }
  
  private int translateMod(int mod) {
    int newMod = 0;
    if (1 == (mod & 1)) {
      newMod += 4;
    }
    if (2 == (mod & 2)) {
      newMod += 2;
    }
    if (4 == (mod & 4)) {
      newMod += 8;
    }
    if (8 == (mod & 8)) {
      newMod += 1;
    }
    return newMod;
  }

  @Override
  public boolean addHotkey(int htype, HotkeyListener listener) {
//TODO hack to avoid usage - predefined hot keys !!!    
    return false;
  }
}
