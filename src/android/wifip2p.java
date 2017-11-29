package wifip2p.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

/**
 * This class echoes a string called from JavaScript.
 */
public class wifip2p extends CordovaPlugin implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener, WifiP2pManager.ChannelListener {

    public static final String TAG = "wifip2p cordova plugin";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;
    public CallbackContext currentCallbackContext;
    public String currentAction;

    @Override
    protected void pluginInitialize() {
        // add necessary intent values to be matched.

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) this.cordova.getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this.cordova.getActivity().getApplicationContext(), this.cordova.getActivity().getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        this.cordova.getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            currentAction = "coolMethod";
            this.coolMethod(message, callbackContext);
            return true;
        }
        if(action.equals("discover")){
            currentAction = "discover";
            this.discover(callbackContext);
            return true;
        }
        if (action.equals("connect_peer")) {
            currentAction = "connect_peer";
            String deviceAddress = args.getString(0);
            this.connect(deviceAddress, callbackContext);
            return true;
        }
        if (action.equals("disconnect_peer")) {
            currentAction = "disconnect_peer";
            this.disconnect(callbackContext);
            return true;
        }
        if (action.equals("creat_group")) {
            currentAction = "creat_group";
            this.creatGroup(callbackContext);
            return true;
        }
        if (action.equals("remove_group")) {
            currentAction = "remove_group";
            this.removeGroup(callbackContext);
            return true;
        }
        return false;
    }
    //test method
    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
    //discover peers
    private void discover(final CallbackContext callbackContext){
        this.currentCallbackContext = callbackContext;
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Discovery Initiated");
                //callbackContext.success("Discovery Initiated");
            }

            @Override
            public void onFailure(int reasonCode) {
                callbackContext.error("Discover Failure with Code " + reasonCode);
            }
        });
    }
    //connect peer
    private void connect(String deviceAddress, CallbackContext callbackContext) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        this.connect(config, callbackContext);
    }
    //create group
    private void creatGroup(final CallbackContext callbackContext){
        this.currentCallbackContext = callbackContext;
        manager.createGroup(channel, new WifiP2pManager.ActionListener(){
            @Override
            public void onSuccess() {
                Log.d(TAG, "Create Group Success");
            }
            @Override
            public void onFailure(int reason) {
                callbackContext.error("Create Group Failure with Code "+reason+", Please try again after Remove Group.");
            }
        });
    }
    //remove group
    private void removeGroup(final CallbackContext callbackContext){
        this.currentCallbackContext = callbackContext;
        manager.removeGroup(channel, new WifiP2pManager.ActionListener(){
            @Override
            public void onSuccess() {
                Log.d(TAG, "Remove Group Success");
                callbackContext.success("Remove Group Success");
            }
            @Override
            public void onFailure(int reason) {
                callbackContext.error("Remove Group Failure with Code "+reason);
            }
        });
    }

    private void getCurrentPeers(CallbackContext callbackContext){

    }

    //on resume
    public void onResume() {
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        this.cordova.getActivity().registerReceiver(receiver, intentFilter);
    }
    //on pause
    public void onPause() {
        this.cordova.getActivity().unregisterReceiver(receiver);
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {

    }

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    /**
     * The requested connection info is available
     *
     * @param info Wi-Fi p2p connection info
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
    }

    /**
     * The requested peer list is available
     *
     * @param peers List of available peers
     */
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        JSONArray jArray = new JSONArray();
        try{
            Collection<WifiP2pDevice> currentPeerList = peers.getDeviceList();
            //List<WifiP2pDevice> currentPeerList = (List<WifiP2pDevice>) peers.getDeviceList();
            for(int i=0; i<currentPeerList.size();i++){
                WifiP2pDevice device = (WifiP2pDevice) currentPeerList.toArray()[i];
                JSONObject jObject = new JSONObject();
                jObject.put("deviceName", device.deviceName);
                jObject.put("deviceAddress", device.deviceAddress);
                jObject.put("status", device.status);
                jArray.put(jObject);
            }
            currentCallbackContext.success(jArray);
        }
        catch(Exception e){
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public void showDetails(WifiP2pDevice device) {
    }

    private void connect(final WifiP2pConfig config, final CallbackContext callbackContext) {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                callbackContext.success(config.deviceAddress);
            }

            @Override
            public void onFailure(int reason) {
                callbackContext.error("Connection failed. Reason : "+reason);
            }
        });
    }

    private void disconnect(final CallbackContext callbackContext) {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                callbackContext.error("Disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {
                callbackContext.success("Disconnected...");
            }

        });
    }

    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            resetData();
            retryChannel = true;
            manager.initialize(this.cordova.getActivity().getApplicationContext(), this.cordova.getActivity().getApplicationContext().getMainLooper(), this);
        } else {

        }
    }

    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
//                    Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
//                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reasonCode) {
//                    Toast.makeText(WiFiDirectActivity.this,
//                            "Connect abort request failed. Reason Code: " + reasonCode,
//                            Toast.LENGTH_SHORT).show();
                }
            });
//            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
//                    .findFragmentById(R.id.frag_list);
//            if (fragment.getDevice() == null
//                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
//                disconnect();
//            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
//                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {
//
//                manager.cancelConnect(channel, new ActionListener() {
//
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onFailure(int reasonCode) {
//                        Toast.makeText(WiFiDirectActivity.this,
//                                "Connect abort request failed. Reason Code: " + reasonCode,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
        }

    }
}
