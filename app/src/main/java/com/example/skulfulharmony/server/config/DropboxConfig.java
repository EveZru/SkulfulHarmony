package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    private static final String ACCESS_TOKEN = "sl.u.AFpBzU5QdrkTxmJdZwIJWkKBLfQPUIbPwRPnhq8eeucQl7k-sdw97captZjhvQGAqORiqN9fZSvqAb73dDC1clfkYgnJVKvF5bClD_VNS4ZBMWVd_e7X2jtsDMupl8tHi1XPlRwjqyQpVyhAt_n1GwflpUe3Yl2T1t1E5cx2lni8yu4irsGTO5wfvlFolBl8bUH30oGiuMJuGL0ZVTYFGRa82R1jUExtqIHkRahka4Mv1HXSM3Q7oyINK5rrG_Z1XIih4bRxX8C3Yj--1VBxBoJAddU018NYp-cCBbwsWI-EAyX6wCsXsUmONW7nC1aqeBU9IVWmF88FAI3_l7gQrp4hjkB5MEp3hkvu0E-vqWVkEe9EpnfTAxsO-wk9Vot-RrGPrPOkuaKaO48Z5HHcRZPuMPTAMKX9LiycwhGiSej3QWABz9HwCNWzV725sDIjREGK6zwYow4unhhCFI2hX77nRbPbQhZfT9bkFVFlyEAcVcsqL6kMQs8nk-9qLEJR6oWZovJu3K0P_O1YsPVsRsRciHMUZsiYBp4m4zSBiWUsH_fXyMoSuYctwd3zeaZTJfcl2hlAAuIlXr4Ive_xHnhJ3zRNmYDwT8lBa0NlH4awHz3Of0oLuTH-YiglPsqe5E8u5BlGbrOtG5GFsLoQlNdWdFpECtnYLaT2ITLip_cT1l0xHw_BZDTCHFLxGeL56_1UAnDovsoFnhTBd0ya13gzmo_IfA6ycqE7HybhRBR-TUKyoKKQUk2D3rTE03EX3fHkgI41Hd79fOYyALgAyMQ7ea5ilwovN-NXY-x2U_TuygzBW0a5t313Zy5gTA3QKEnQf18QX64B8muqJl9Cin1HvUgjpCiatamGkeVg24sL1DEwcdI7J9lkyTcOsU17teggxJmqSIx3qEjvxPlXHGtVBxPilaEIPiyJTds6qFOaL5S0jYAh294ZXdMRa2r54VoCh0wrNyI5sXJaghoCa_R_v7KTwu5gYyZh4XZ_anOR4LqSGR6bPvlTNQgQReeNww7AHDoWWKFp9Pa6K8Bi3qxO0aS6Ix-1e-yMd54iXPBA-oVBfosvEDFFb-6mSsNh6PhwkwJr25kZK5aSx7mnygjNYjSF3wlDeXyD5LXge4kHha3HhIvwA_NT7rRLg8anBX5uFzxq6Kc_sugJo4Wca20Es9AO_I7DriDMKXeitnlTsJjgEnNoKpU2Rnu8x_psU37PdUmHPSLgbg77c37Zvyxu0ZtcgBmQ5c1z5haXGDV6gWVchfbsHVIEspRDUt5OLEglbtFC4WqjlRuzQCQo9LUtFN3S5js1urJhijrU1O-wl-kn6lMPJ7W6r4rtruZML33MrdnlGjVR5260aTiyzt6BQdz-5BEjcnRgj6h_6ab1SNgphr5a8uxjZSuoc2pLFxafOVGo9_j5bk_LuQumcRkSdVjlW4Mt0qPtJdXVpq9LXQ";
    // Constructor que recibe el Access Token (lo obtenemos del flujo de OAuth)
    public DropboxConfig(String accessToken) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("SkillfullHarmonyApp")
                .build();
        // Usamos el Access Token para inicializar el cliente Dropbox
        this.client = new DbxClientV2(config, accessToken);
    }

    // Método para obtener el cliente de Dropbox
    public DbxClientV2 getClient() {
        return this.client;
    }
}