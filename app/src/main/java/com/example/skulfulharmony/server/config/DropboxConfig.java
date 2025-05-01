package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFu_EoVMZaPw0I8sbdw5S0daOAbdsCGX8NzyQ4SKf8t7jec1t-aPer4U9zyQD5fyZ_6AFOqY3DK7D8V79EOj3sULT5L006GMVPoDEfdxCUVjj1KYY2r0rFW3UzzEf09LXOL_HU-RVtjQYuUg9zvml3vEb05DMDU_TvOIeensf17LDtaYGiMU-M4GvUzYxyfQf8mSLYyhlXfFqij6zj7jhHhY0IUk_XVTvjtGLhNS8q0Rz81Dtx5ZV-JY9zQv5HmVLD4J5YGT-nkfTKyM5hQ38aodSwiFObmGrBWteAzwlJ_EYpaFC-byRwT78Yaip5_C4zbNPwAEvZcv3-1_zJwc3YJfQ9AiR_Jl9vFapT1j1g9LUHVDd5yfvv7kKQs-P-aZZi1z-Ue19nSJSh-MRvbGpmNwDs7nCwVWTdjZD24_nCST-_V88sRNmc3AE4Rol5eIxV72mnc80yQpLXgWzwp-_pg5Fpfq156NUZ-t8PjL5P9824_mLoBOENY16vkEJsQvKwYIRYrMCxyPq0gvQ-ZV0Ga_RUqoFICPjQxOPMBNdbL6Y-qS2s5UQJYeobFzCji-iDBn3Jcg4ZAHRC4ae7xc-Ln-rgH1TjE9PcLurG7xcBMrAI9UjPl0k2YXyNSogZ5bCvyWnQbUk4k0-E8NruogE4gyB05u2NlfXc5zUE4_uOW047SfvzTITTNCCXMpU44XSSyY7hwEAT9YW9bB8eya6WR7A0zbZ1tv0oojS0JzaCTTxNhZKqr-l3JnkBHKniAfWuXuxe7rLEi1x_kgLY7cdGY_MxAZQtUQ-u6kEbWDVK8xi8d45g3NN57A-uuT2GBQrbxe_5sM61H-LLf2wK6iQ9iTtzkdDhADt3o4qQveAlWM-BnOg705eeA6ues8D1Vh71QaGDZNOKqOqcIHNCcO3olxt8WEA0EzpFmBGVV2gWqyTHgbStX_aRtY2Poj9NamAla6SSrTt6s9AH-aouMZ6BEFjRFOyQsePcuOT8JmQgxZVockkUCG6keFC4RDvpXIuOkwLLDvKdmhNWTJKtI7ieIHZnvD7_K9lsTXlGruVFzag-YFuqxWrb1g8bS6hV1M_Lxlrfd700Si7gR7oFclZlcIAoA4XDXew_FZZrQDH9YGnqgDqjtk1t2c83X24LYHzmmjBI70aJ2txNkw9Rekhdzl-U92ZnoZ1t6Q9gpbwD1zO9ShujOKLMM0aO2X4tBEmd-m9tepNjTC4aflBKmkTxkwIWMWxC-45IfG86mO0mdUualZ0G_DsAQRFwCP6zwAoYO-OCuZH_z1i1oHAaZpKoOloY6BWuvQq_s-GPM_CQuCRGs0ziKs38BlDyL770Fdhfl7b6IEXtiFpgOJ4wJ4Ihtw0fglDumhr0z2LN6QUNuzV5T1ooNUo3JIjgnr4KOuKxpM2YunjtAH8aE3zhvbEmKKR2pbPdVepa8_kIkp8CrQyQ";
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