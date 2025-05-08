package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFtXQhOi12_q0AzCPmBCe32S64D9aqDRlIPN4aBs3M4_TW4dqQQjqwVYIdVQXl-XTN5ZTmHZ46W4oCHdIQ-JMbeIFXIoU-2M7A9rKxJH5UNfjroajiqyLCSqeyz2NGPkzdyjZVXpS-s1EvP9ntXq16qZx9G8ZbDyoZFLNK5mRjQprrWu1AFcdEzHzqBqG9jj_HheHOxLv4PbMXQy57ACBryunUs65RRtETyVIdnQXRODQJTmAWrtJ47__9P9v1H461HeHsFE4Um2IWbe0bunMFX7qsIoT_Vod69CvPN5QPGDsnsnZ78iAzvNEF_YqI93wlHuahDWhvxufUqZoash4uAEzsGhYS_ECwo_ltZ5KTWTAj40adWRViDajH6myXofAxd7E-8AIDdgt3kxIrpROGV5jLG0KB5jsOQcawhE8N0Ynhar7HpQOijbrzdCcNSr-x0J6EMkmV_JshpUlTgovVmdjsOOHZ0u_Zn_kxO3Q2ynZv3UEYGoIL4ogsmZb7siU5uSe48_N6DW7RVVlMLD_HOtJjMfWGCR2fpft8-AAy9U461MOKf1AxCXT5vsRr7lDbU0esYttLYs_A3RIFf1xGYlExriopCJVNyWFV1xdvE-k3mOBU-ahhxnMOm-DaaHJD7zHzpt0YIGEGDV3qelS_JEaZ5pY6JvRwBIE9ho2cF395GfXn0Rdn-AJSi8QgH8HFvJnbHpIim58xBSREdmK1haglCNytXLT1eCd3Q8Iz6sdtlvKips2E-ccUyim0Aa9n3qTe1KiOddYimNQ0L4GTe2ZuccVZEfWyXeh5EnZVkxj-Oh1Eulyv6LFGOaA9F4CITH42NEccDJr8VGWiS9g7PFIuyjtSJjbX0I6uU4hqOSaqHqvRWGO3eAs6R9I3J4fT8-JKFuxogxO1tIQg5U9swm6bN22u9RXrH74b3zfzDt24sps9cdRexy3flsqL6uZf6I2cdsO16Sxz-xoPcVaUQAF0opRavpXMBfSHQok1uxHFvu0fC_tmdhrROtcCm77AijaIG5EWSD0JYbMuu5Eo4PW9bTEV923v9xi6xS2EqL7L8xXwaf-0QOk0JxV6mRTOgwH_UQEJuMtx45z2t30Xja3-waNqpLtes-xDLFuP79gIOkaIuKFIR1ibQ_jZtiU0rEUEE36U4mDBc2krrTgUcfF9CvYtIwHY9M7RnYDFFn8toAcYPaN23MUcHu7-zR65dA9-opR31UF5wVmx2RsSS1qRhurwV-RWeoNJyqBp9XSTqsxT9Nmg0-bIF5BnAjEfJK_NgJxtgvaLpgXrknrE-bKAugPMsWTqumlKCeNDAqtpiGeMSGxsiffPThOaVu13iKIhGNSb2urpmeRybYMvV-ykpEBkuUgikx98w7crsMoANtrJc_AHdf9iIaP6dbdp9FD3wwH18kXW2KDj8_dRmu16bIhPjIlSvWNvsbDjL9LA";
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