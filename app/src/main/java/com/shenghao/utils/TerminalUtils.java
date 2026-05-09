package com.shenghao.utils;


import com.shenghao.bean.TerminalBean;
import com.shenghao.utility.AppSingleton;

import java.util.List;

public class TerminalUtils {
    /**
     * 设置当前设备
     */
    public static void setCurrentTerminal(List<TerminalBean> terminalList) {
        if (terminalList == null || terminalList.size() == 0) {
            return;
        }
        String currentTerminalNo = AppSingleton.getInstance().getTerminalNo();
        TerminalBean currentTerminal = null;
        // 遍历服务端请求到的设备数据
        for (TerminalBean terminalBean : terminalList) {
            if (terminalBean.getStatus() == 1) {    //将服务端默认设备作为本地当前设备
                terminalBean.setSelected(true);
                currentTerminal = terminalBean;
            } else {
                terminalBean.setSelected(false);
            }
        }

        // 服务端返回的设备列表没有默认设备，再次遍历与本地当前设备进行匹配
        /*if (currentTerminal == null && !TextUtils.isEmpty(currentTerminalNo)) {
            for (TerminalBean terminalBean : terminalList) {
                if (TextUtils.equals(currentTerminalNo, terminalBean.getTerminalNo())) {
                    terminalBean.setSelected(true);
                    currentTerminal = terminalBean;
                } else {
                    terminalBean.setSelected(false);
                }
            }
        }*/

        // 仍然未匹配到当前设备，将请求到的首个设备作为新的当前设备
        if (currentTerminal == null) {
            TerminalBean terminalBean = terminalList.get(0);
            terminalBean.setSelected(true);
            currentTerminal = terminalBean;
        }

        AppSingleton.getInstance().setTerminalNo(currentTerminal.getTerminalNo());
        AppSingleton.getInstance().setTerminalId(currentTerminal.getTerminalId());
        AppSingleton.getInstance().setTerminalName(currentTerminal.getName());
        AppSingleton.getInstance().setBatteryCount(currentTerminal.getBatteries());
        AppSingleton.getInstance().setCurrentTerminal(currentTerminal);
        AppSingleton.getInstance().setTerminalList(terminalList);   //保存设备列表
    }
}
