package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFvIwArS2PRKOFjt9Ri_znM_7V513jj2e5t8RHyp7KgywuEmJKj8GG4q2htqkfL_1oEVGZ1KBpPDib4fWmlhw9Bubd9bgVlFQJeyddBOOtr427MhgWWyYXAwXllOeeQ40cscSHzvLBxrGHxlKIia5CTvX1_ZAidgsDjq7lhYCE9CJHdn9aJl6oouMT-FXV9fZtPqOzNknDbGkagd67oQDloeTyh-oRNGYiXRZB02kXphKohhTYK6VfKucTjLyKyYUffHGvrtLY8l6MDnQ7qZREDc4Jqr4iktm72I-FzkSm3SDMZVWu5ZHnXheXRTIQ_Aq81QmZav9ap0_FxH_mWcGr2RJImNytqey-hBaqFUKwZpXKm5dAErzwcRwW8QluyPST0nOEK2c71JLRJUqT-z6EAVWwgzguqhV-E-Py_5pgTvTaAJJsn5K_Y_nBhDfsH-rIOxIEGbsp6vKu9jFBNAjWEYi8z_8ZIthYu7M4Be7BX7Mkszeilsw5PYjzhdzZKAgEO8ndKaAzeSFlPg5d6OXMFx7TLCRqdSlD_bLWNu8hDtEdz4Ak55s72Zsa-giw49A41H4YfORCohkZnOC7K2bIjrOd5GQd9ObHvu_KRFYPvZUAhP4TbFYStPvVtphphTE4G9RICY8Uh-hF9z9B-xvSqoF6xtwd2XLU1sWKThouY4XdpbWK_mz829Q0hcmACCDYU2Fbd0W38mHi9lby1oZMn5rWzlU4lssO8abR3Je5WK2vWy6grPdWYAday1K9SZapONa_tfSgDUBqhSFu3sPIsHoIEy4uEK0f3v5jW-MDTxj7m6Gt3LdhbZHmFTl5baBexN3tAW23MovLlOirUmj6h7OmQM3ZBI_xE-RJrzRWGMV6OMvovfvLteb95MyLln9ZAFf8drR_HByxmGNkJCjHoolgNjuxYdwUpRmm7fiycQ0tDreM9vy3W2qW9aqhHsgrQxuxO8fUG719WB1ifpmUOmNA6rTKHHDcl4GXx3IcgmzdmgBr8u6qJcu7T36mQmf6-Z6YVtdLoW5qxpHCosApaOMpHHbGwNfCG3r8E4mAanspA8vrgxsuK0WWaBhqzQtEK7BGOoam9eUDybHIcMGKq1YaptOnRkS3_EUJ-JogBtUGv5lCAAftrkYPl5rWZeE2NfsM6uM6EufYHq82KwhKT2t5frtF4tH4VTcF35NCyn3dFZHxv0svHOSsur1ltbHR153Fxq4ihabFcKQvImV3qenMf7yrre72wWtimElLAibpWSjp2x9qVcdiPsBKg8mA8ueg-SPef9CxPP_bex_g6mR5oxCH1Q0ttPlNInrRyovvhtmfegDDPL2f8N47JtXiHzeHsgQRcFUULYkWsUkIx7Q08O2n7Pd_6ol8vOFi6sWvhzbFBakJYnb8Lp3oe7hosSJ7TziVKumEBRUEVbvrpPmpLA3j1WOo9xRCLZ_V6UVw";
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