package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFtxlcQJtR_U2AHgdFyX43Q09mNycUdGyt7AWBkgCHrUiDwJwcdONaJ-hgHcvOsHpSD9v53EqSk4TtJd_LTjy9Hq2Z1i6GXQo3_bocSNIeRN1kcQb67pyvgfaZhO0MylUvSTSLRSvhJRZhSvs3_MVKaZSrzxc6lG4WnJqmQi64DuYA5fHlI8zX4NjBpzGlKzJ1UZaGHCcQeLahob8KD2WGE4NZM3AwfXMxQiF1q_KtPHKhIIWeBBRUYkb8b1gQG5gX5XoCqHh6WKZ-kJe4q7qJZPB23ljDzgCWFjMZ536D9Zo0AAui5wPkNFWdEa5etmZf3tbV4iN2vKBouAUKAw7embq7XS-iwJKRHNbXrGUdj_Xre3DtKH3fb6adlm3FusshLIsHKxxwxSEEhGJK-jqRRIQ-NROtUjV-vFJ5IjGQqOsRtREzOIB59TZWnO5ydxdAHLyVIbjxQz6A6iaWvDTwWIEGdtNWnq7vPfFaKuKKeBacsKHC7gzo7TRKLg-XqInk8mtRsrSgX3IofA1lKNXerSx_v3wr4k_4spGLRbwr2Zjm-SSanjmNO2gwfYkT56Ux8EN1l6uNXM9-g5PYSJbkP_3DthXw1vaY7vPtoZUJqNAxg1t1Dv7ovyj0fMcbp5pe9xVRDCF9og346-en_ZgDA45NQL1npy8G4zxwdMdc4HLJrOOYPOL6IyWBcKM03TWy5BWCaCRK1p2rqsZvioDBLLcZV2_Iw9yoIgCukAIpV8Dn8_EUFQXswGZj3vzf1I8GJcJOCoFsdNvVt-ZEB0RJv-FJlHdYKEqkgbPmSKc6bd4fsGq1LdO1Kl9cnaY-StkatBacBQqiYnaF8tiTQL7guy_7-iiApXe7PfjvnPgwvm5KIKuqTNIBIhZ_ZA_wV_OjZ49yjFZCD9uhjLyf7_smOf3kTCXEzaYKAPr5scqaNswEOqX8HhoULTRWQ9CqHGmyA6a9Ry-1VyrVeGDxkHNyL-nGziEBzSPWcao_Vcy477V7dSTA6Mwq5vR25eR8lZdYj1oUnjc8ZwptpjncI3xxvaGGuDEntgJ49NZ-b4Xa-BR3aX_gvSeiJZvXPBsuyC9oSN5Wnzi08aGuRi-r20u3bSaPlID7mhZ1L7z6qy7NTQH9s3ToeyUmf0jT2VDotdCNoPMs05kf7E48mzr_sdlzTSDvRYNwNuHMBIUoOK9TmcUUNG0GMyrPXjSoGD-MNCoYWHmGwGZ6Qur-5OfAnK9Xb-AMUgmJ-CzrvaL4PfP19OhL_1zMAX2lkO_phf91NuJbXOUcRvwAfvOqwqCfkeo1kwvt7go1nLn_gW1bQK9Qe2RcBiGvJ3_a7w5avOUs1gAlJni2yrYvNKK2kybzf13riIbebMvZWFtUhym3jTkoRC_FQYMHArUMunaAISUY63BHECzIvze0d5RYrppJu9Hkn3fNkkB5JpA7XqUTLf7NkxeQ";

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