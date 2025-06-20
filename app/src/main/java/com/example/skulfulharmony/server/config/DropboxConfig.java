package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFxIKIREuw2v6U8jltb8_KCUE7Zq-m4Y03q1PGTvHjCQHfNFzooGzjSzDhuuy4InbTuJZurnancg06-BL_tVMcYpWmkFK2xsZsmC9N28lOI0nflIvKnhk6S7kGwH5LVeXLlzCoeicapujnAfeex2vGRTNOibu4XAop10nsTdGIl-bfZMh0x4GSgeaUPpR8aSWKnjJVCe1clW8ipMt6Ih4klqqIwq-TG8o8TWl3tQGzdPidBFPrwJu2B0EDbg9Hbu6RhiI4Poj2lf4dVdgreSpeQVnR1w3OpGTvrgqbhYDbAbveJS6zdETQCh54jDlmhIwlKHqHHDSNgtCFmhw6MIEbS7JUYNSR-hGsqgEAgjTfzM8j_L2T2TGZKGiV6_e_9V40ZmBcOTbsKVsa5u5rJsbgfWDHfAIv0MuoPoC-yPq6l_cn3llY05f9EDwrg2n6-pCdxPtbc40SncqhjoLHrxiTPUtG1-cfvultttUr2kKIknEPJuvG3GkQTCvTwuPnJBB5Qhs5VxsGyBvYFnIdQNs2vk1AdKFMpWQKXEBG7jiU2KTk8zBY70pHG-IfhlWFdC-koMN6gofgQs832e_fkHZdio8JOkhia9wak3iy7fIFtT7IDryOfLaDfHMI4wkdrjaSyRwGNEevTRD8zFFmuyRad5ACfHYc5IAobafhPMdZhJ1pL9vGjRtF2eCEgIvkMUtMjMtddX9lhJjHBCOpE80oq9LTSfgCO-PL7QyyTzcFLHzcPHniJm3e_ixmIBnp78GjYQmssvFXFCwXBW2LiPm-_e87WQRIRkLoRvEWr9OUt0A_8huxIzBzy-jX2PSzmyftzyxsEh8MXGLzNUd9tSP1FisUTqNu4JGvYwqVtRAjvyqYqjW55_Hd3w4aNPp62mHPh48QtKlLg6JgqsoeULErFgnPLB9FcK9C0Wxq6Va3AG-ILxdG8zq7i2RPbMAXyhA1VuW6UbHUZ0UCcRB2Cm8q3Wq10-Tq7PYvgIoEdHkvcGCZjndm40HQ1DUDFublGdx2dlQismKmOK4JI9M8AWBQzYiol-y97wdnxUe4pc3M3Bfjb_bydzuf7uD_MJwRPKrUfnt7Nekq8FRCWDwBYeVUrvQKXLVVC8tA4dl2pGf2p1kajLhVA9hVP3gB98VGZT4pinaXsEgmZ9Amlt4rs_e1H01-Q4Eaya2BGRMRUB1BBxkVFzkKkoHByXm-lO2XAh-VfEph-ScYOrhzAojAAdLFtgPk8ryqvUvdYszo7MIUoQBXFx6av-ukPfxVaHz3hzNsM_ApOMbM-Omk7lVF8uVKJpIBjPH5agdy2FU4by2D67xNJ7g1wdfyxEPIQ2TEqM1mc2jJpxAeqEXGwx9zsGC1MQfq_sd5e8zL03YssXcX1pQZ_0x4Eq1fk4Q9Whc5wK--RhWUKr8s57VqfAJZXL7BouPIM0ur9xP9Dnt_glli-WpA";
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