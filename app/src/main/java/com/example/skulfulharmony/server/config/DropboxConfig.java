package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFqObO_2uOuiaPcHAXjLbEhATsTwCGi7yK3IxMEbDpg_p-sKIFvb91hNShCN_lmjZYAPaQVA7XmvNuQe4kEFq87lVTvgPcB3KpNFXJZY4lkM_F2QhB-0VpC7jEaShFGjTqIbCXyPeLLyF6VAG_D_I060pYTUFzX6evqakBlaHarFBgdDpTCG_f2rfFF8k3YtRglLAzJ1FEF6x8U-6eKmi5UMigBluq9FaRxeQkUOPI6I80IIT29WfMuvl5RzrIMgTtGAs6lUP_MYaXXNESI5Np1ME5eYg-BCHlNChRhn6-t7yaiyPVR9Zzk3BGwT2lOYWMsNolCug41MNUfDfDSQTQecKgz0qTrif1z_vj6MV70UCl59EF02IxHnA1Jq5QirQRtEAV6YX50XReGheWSN-lZmbWB5oEe-36q2HIf9KPlkQvBxJMq27r0_5Jqz1pBZoOvIl9y0ntIV_l_5LS60kau35WPS3FCtUK6uimXK9jVidkj-XGjtgt83eFQO4Za15kDQHCQkBs4w4o2b5Js86NrRiWweyb1XHpbt0b1LMK9FjR-W3cWvi3ar3tELwyvGWzHy-AfH76PTPuVfLJ89DYK9iCjjhnENF7m-_1dKaviiiUH6lECyU0Ykc2kyhgomutjCRrZBaDtzKsiabqQtOSj_eg7zHS6uEtCk72rC7ParfDzqZDFA1E9wR-xepUtfCvpzO0Zf2pRIvrqeHRzMsTrgY2M0-ZcMheVJVXBYALo4Cle59XryCpotu5bKXCZ7TtJzLm3SNN_u7NQaicqN7XHCcqAxOjYNP5lbiPV0tW4E_iHOlhLUVt0ZG4rJG52HqpklZLjm6ICuT6Hny45zxHtx0oYuxehdiS4QsTgzZQfu9NXrs3cJqkkTNm865hl3IHtDarKzrIGSYXBuhjwJeQzNaQQ0mgwQnxocSKKS0giOml97eBCendJPeMvWkm5aPBxbJhUf_QTjKgbCR4f-dfQ9mPS0aAL8zeTP-hwtsuTeesgAvno1dRCzLF2huABdDDQ_W_6TOlsh6PKru6H2nrDxj1sULTWBkmko8-IXUIY4u_z2aD0zdWo1ngonL8BsgURArmVBZpucsJD3K6Xax1EmPw7lnY54_fCvg2smP4R41RAWhD1gPzVTowj61mnbsZW263H-0_O01QwPGn-WI2x-t0Nt1NTOXk7tVpI6DUdYgqONdfcVT_E-RX4_VAcPwizub33GlRVw0OLZW56_iDudUlFbuVqHomkAZofO43kayclWZKVhyD6oBZ5td7OkzffJ7-9rULZbYgWKLT4Do_iEhJbi0Xx8LDtQpI9FDxrn-lyutm5kNkp2qj67eyxkOCU0pRYcZI_h5-kmK-EVF3ftFX6a2VNCI84Cr54_VdPIaExTYj2lOZ-bGVTL3F8Kj7bFnXJ2Sw2lq_hUT7YSMPnOMOdQQhSUZcAdmgV4dpuBSA";
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