package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFytJHClkjhgy4Y4Wcx9G3Uv10GgYNmX1haBPVyUoPb8h-T7edjXsphBviPkfyBQr9tbT5cGbFD2u-Xuo4BbpCp4tUZKOconVIXPoambN51WTXB-hpoBPjfP9SqhWUkxJZtSazgS1ZML-MqVIKF3u7xQyJzNEFSOF5_JJuaF-clEzBPGO5LktaLg9wToYKSdiK9ZDeZ9O-Rji0_v8ZWt3G-s-y2E64kyli2tUaAA3akokCXcYRsNgrzMCxSycC3ZdWIt3bL1YQDM4UWpyDWJ8vAbm0CitTMrpdV8oCUhRI21-5pO7pIjvkH5h0Pxqu5YYF54-3N54CZkarz9Bt5gLAM5200AWd2jJt_cV5lWVyczHRPBws47dtVUNYLKJlrUaCxQ8FuyYTAAuOU0cgSkCqhad02CNky6XewLRav9lzQkeop0xNNaGXtXMMXeW68E7Jjk20gAk98sbP0Ov4DKVx_hDWfbQdWw5bjuiMw-Dqvz4bm7yLeMYzjsjwTLB9duSMrEGRkHvBqRek_EtUdys5MGG0o4GZvAo7O24wUcfM9wXOSirc8uU07hJXtR_NdrN6d5ViT4L_yzEvkHu-1ua6de14m8y8i6AdPzC37AMlzSpXtNlcUU8HvQwIffPBEHbMtbmbk7JpzaDlOJswvt7ME9jC7sz1Nhu7HzZFrOW_WkyaXs4WXAXjcVcccf-ecs1H75da9q2XXxBTuqFfyxx5RYvIRBR_gkYBpRkA7owNeetLRccOeeudz-ndTazohUyDejiynseoENBfw7ErHa_ERaC_gOOw-G_YHghqS1FaRORvbhn7FG4Yype4vNTt163KWp4vBw4vr9wANsSgApM0_KrCRlXUyRPzn-Kc1THsrQp5rrCsqMHFc54ujG35jVUOsugBYksEvtBgv2t3Jb0E0ptucipd7On7mg2XyD-05dO_11O-ZQ_BFSnvnMhMR454ciOJvFdgvrjSqOqjx9dVloxdZbKEfz2LntB5_9CWgvNMI_xhL6itJ1BPtsRF4kwa-Qeu4N7vbp9c4IB5u9CWmY6xzgTz8dNPHcqdH1H5qlaN8qmFZ6gFi0j0UkNRH76d78-0CPzbtAmj2vHp7sQX_LMURuwdM4TiGZOnvBn7lAj5I0mX7ese0q-vLuCK4S6iLzdWuC62LI6wOHradGp-AHn0ib8ZKUUgSTKBuE8DWGkNH3PeeJjKZC5z8F_RWf34sP7Gxj4K8uG3Ge_MqL9KXFegEeOVRc1QSxuOzrhQ1NRfaUlkkX2KkjWgB0Gnqya1InvwpDCGnvhDVxkI4aYIvBiY_npTbGCZBC2dFV7QhLt5gyM0eCZi5KyKdpvkFSmEzFdFBb03ll_z-8HwMDC4jlGAncDHlHeGIPRfm-ykkLQoZ5tmlruO6La3Y4IuOK6Yf-qlL60nPOLm_wcvMoQ5gUI_BlhJhcgagK70vgp7fNtg";
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