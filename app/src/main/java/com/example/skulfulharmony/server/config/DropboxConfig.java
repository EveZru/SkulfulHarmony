package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFzkRPj-uaH3DX228Jw3tK6kioPbgl6uO06EAV-zo7Yy4iR-2MyhwHJxbi36Op_D_lyrLpE-gV8Ih_T8lYkavR3XTBNw7eFT_2UzqSU2LdQPBmelqSBu7RDG8K5rTxGJkFIL2ZRSLu7IIAxTiRjRNO3lbJEOwn6g8jR-8vyrfuXk2UrC3R7PaXnHwF2hpxKsYMJOFhjfFXJ6F3UwSkdZPQ0pzA-l00HFrW3vSjDYsAkRfAGwZ-JrCy1_IR5uHWRJaTTYtC4vQppyuB8rdeXnLCB1wGpWvLPp-rwCvFiDjg04C9wSTvWok_XNZ-FX-2EKBo4kDN2aU_99nRP8WK_fHBRlyrvmP69faAqVtwlMtm0Q8Bs4GkIJQV-CwkRHC_PKGjJ2Z97uJ41P5g34CRPJsPMBst_FB_S8ka4UBLZTPuEHeOeQIZj84otaXDeW487hkUWFj7d5dsSI5TEid9ekuRWccCwt6rTtIGF6QzonH7mKnJG1QMSdMiweRNwfO4qVMYxd3LyGXrvDzJshESrxZPZOi_SAmvNV2-z7Yka3-dIxbo1sQ6fiOy-CtL0KV7JV5uOREg0bz5ZaXv2xw3JWCH8lwO528nI-cfUjz75CqeIUnFdd28DlqVdYtF7rOv6GyENemQyqXLujTVhSZaVKe2qJPAUa84jGklX40_Okmr9zLNwS58_L9OLSx6O8nyMxE1dSLHXxTPWPmfjBhZRiWg4PxXne1eVx3D5R5AYmnPTYYc23jgwsMS_2Y2Vc-3cqPnVov817Thb6MGzOp8V8IjXVDDq_y6i7uMKmPCEkqs7zxFGQ6H19YnbeChimpN-7dQwPULgrrcxwexcn9dfI413e3Dxw8hpV7-a_RqJII2kvAfKvPKPQSOiR5ef-R3SHDi477eoSKYXXqQOJplBB8eqPV87_uveUlntT8vSkTL9WM56lo1VZTzmnhyhM6qd8V5gMXp1188Ue1_OVT9qYKJCCdM5dBq2DaB5qGpId9Q1_1pDFiKru4EL-k9Aabv3cdn58cFsSSg16kQVCZ17bROXcfOnGS-y5p_NkQhFxtx9M2KvyLpDBuLUvnJhkP36pLk8gGuYVM6hecxd9S23AG_DoYMWwESB4xXA7-uDj5nml2Wm3Mddp3_FQMnNhBgEC0cpEzd9YVXzCXOINk8dwiYCFUAFRd5FM5XIkQNoQCMPwABnvSLgJ9c7XYHyKZ5mKGHT8odIIlMegJG5SeROfpH3BOvRGBIcogN7luSadt8VaHH-OHGlATTeYFJQkVuoM0iogvUH-1iBifNylpTmD8ubjkJUJcsWXkl0y1ZHM7HJkLy0hN2SfgLiNObfUuT7x_Q8tX-Zh0VqL5aYzlvizKAOLSK9O1pXTydaOfiBFHxr-UwLEfOBlLdHbAxlqMFe6XNX7LwZqVTpy3vZ5lWK2QPJTCwyXEREHjgE78U5JuML9Qg";
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