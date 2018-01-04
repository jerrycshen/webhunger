package me.shenchao.webhunger.util.common;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class SystemUtils {

    private static String INTRANET_IP = getIntranetIp();
    /**
     * 外网IP
     */
    public static String INTERNET_IP = getInternetIp();

    public static String getWebHungerHomeDir() {
        String homeDir = System.getProperty("webhunger.home");
        if (homeDir == null) {
            throw new RuntimeException("未指定程序根目录，程序退出......");
        }
        return homeDir;
    }

    public static String getWebHungerConfigDir() {
        String configDir = System.getProperty("webhunger.conf");
        if (configDir == null) {
            return getWebHungerHomeDir() + File.separator + "conf";
        }
        return configDir;
    }

    public static String getWebHungerDefaultDir() {
        String userDir = System.getProperty("webhunger.default");
        if (userDir == null) {
            return getWebHungerHomeDir() + File.separator + "default";
        }
        return userDir;
    }

    /**
     * 获得内网IP
     * @return 内网IP
     */
    private static String getIntranetIp(){
        try{
            return InetAddress.getLocalHost().getHostAddress();
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得外网IP
     * @return 外网IP
     */
    private static String getInternetIp(){
        try{
            Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            Enumeration<InetAddress> addrs;
            while (networks.hasMoreElements())
            {
                addrs = networks.nextElement().getInetAddresses();
                while (addrs.hasMoreElements())
                {
                    ip = addrs.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && ip.isSiteLocalAddress()
                            && !ip.getHostAddress().equals(INTRANET_IP))
                    {
                        return ip.getHostAddress();
                    }
                }
            }

            // 如果没有外网IP，就返回内网IP
            return INTRANET_IP;
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * @return hostName@IP
     */
    public static String getHostName() {
        StringBuilder hostName = new StringBuilder();
        try {
            InetAddress address = InetAddress.getLocalHost();
            String name = address.getHostName();
            hostName.append(name).append("@").append(INTERNET_IP);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            hostName.append("unknown");
        }
        return hostName.toString();
    }

}
