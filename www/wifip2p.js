cordova.define("wifip2p.wifip2p", function(require, exports, module) {
    var exec = require('cordova/exec');

    var WIFIDIR_SOCKET_EVENT = "WIFIDIR_SOCKET_EVENT";
    var CORDOVA_SERVICE_NAME = "wifip2p";

    //Properties
    WiFiDirSocket.State = {};
    WiFiDirSocket.State[WiFiDirSocket.State.CLOSED = 0] = "CLOSED";
    WiFiDirSocket.State[WiFiDirSocket.State.OPENING = 1] = "OPENING";
    WiFiDirSocket.State[WiFiDirSocket.State.OPENED = 2] = "OPENED";
    WiFiDirSocket.State[WiFiDirSocket.State.CLOSING = 3] = "CLOSING";

    function WiFiDirSocket() {
        this._state = WiFiDirSocket.State.CLOSED;
        this.onData = null;
        this.onClose = null;
        this.onError = null;
    }
    //plugin test function
    WiFiDirSocket.prototype.coolMethod = function (data, success, error) {
        exec(success, error, CORDOVA_SERVICE_NAME, 'coolMethod', [data]);
    };

    WiFiDirSocket.prototype.creat_group = function (data, success, error) {
        exec(success, error, CORDOVA_SERVICE_NAME, 'creat_group', [data]);
    };

    WiFiDirSocket.prototype.open = function (ipaddress, port, success, error) {
        exec(success, error, CORDOVA_SERVICE_NAME, 'open', [data]);
    };

    WiFiDirSocket.prototype.write = function (data, success, error) {
        exec(success, error, CORDOVA_SERVICE_NAME, 'write', [data]);
    };

    WiFiDirSocket.prototype.close = function (success, error) {
        exec(success, error, CORDOVA_SERVICE_NAME, 'close');
    };

    WiFiDirSocket.prototype.discover = function (data, success, error) {
        exec(success, error, CORDOVA_SERVICE_NAME, 'discover', [data]);
    };

    WiFiDirSocket.prototype.remove_group = function (data, success, error) {
        exec(success, error, CORDOVA_SERVICE_NAME, 'remove_group', [data]);
    };

    WiFiDirSocket.prototype.connect_peer = function (data, success, error) {
        exec(success, error, CORDOVA_SERVICE_NAME, 'connect_peer', [data]);
    };

    WiFiDirSocket.prototype.disconnect_peer = function (data, success, error) {
        exec(success, error, CORDOVA_SERVICE_NAME, 'disconnect_peer', [data]);
    };

    module.exports = WiFiDirSocket;
});
