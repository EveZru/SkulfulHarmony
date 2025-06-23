package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFxWjNkZTN2Wgv-7ET8FMY_nUTM6ObPeaOJg5eGi-EOlbiIRxPeVXeZPz3MisNplP3VSEaZ7RZ-Yvt-pSz9YuMUxOyJRQ_rib2gd168flXWD39aHB2fgtG0ML6fLC_OVLF3gxR2zIEayc5ExAH4W9S02gUjWtLehVh9xgXa0NPpxhknVQW0Ew-T-MDQjOETyjp4VuF4aEn_Q36GLiaTzaTYa5gb_uyOdiGgmtje2IhXyf8TFoup9XfW7fuAmi9HTHZB5D7JmecdUSW7DIW0UY8_RBUykJ9HX2m1PIaWbQSesStdz3RFIH50ikIQBo_okijXnOB9KwS0fACKhrtVE528nq-qPrmaxdsv9fXz3Ceesb6Xvpf0q-m0OWdLgWf2NXrsvZ5bBi4vO6vBjJj616Lq1kJmufFLOr_ysqERdUB_x5ojgWHr2ltqbIcaLfr0erGCSLHkKc2-r8NsMWF9z8OoJGoMWebZlxM-jJg5_rpCnKz2RF7ZtJL5IIE36gjylcJphYhPKlq9f7mT8ytp5D4BAV9m6SvMKLWg5uelpFqSlcCj9gg36VavV0otSNESnel1o5O2uoT0IDMoZv7Qvh4TMw69MyjGgiqy4WRqEZ_4tG7oVOoM16PCRqgkkzNYSUWW8euFrSLzMnXAvlC3j3Cq4FmlVticsdG4jTJKkj5UPevyYYSlaenGaO-_S8WuzoE4V2J7mORV87LwFst1Ibri4R4jo403DPtt2WMmR7N-siuLxIVkVKNOu9hfvOUQNb4mJori1KgKTfGFrKSz79m6UoZm5Cexat-QDKjT7COCau71SrG3XgDA-b6gQL__rjSWq4CPsb39FpCnSaJHvQtKcWbli7Gerhn_NQq9rw0hRk9_XB8SKrLPet9ZFLfeaHLzqsHkb6CHnkiAQhulLUXixZvfQxmZeb5YbzACouhE_565VTKi1Gs-1UGOAvDNfJyhlIaFUdCL5szR3oaGCe3th4qIxELR00aIE1cv8n_S7JJq4NCXzEGLq_FMa-oAQJSYh8XGVv_bbEkC8MzQbB3Si8y8N_VABctYhVGqCf6dS5Rc2ueIjPH6xvIjbIVViHYA0mZMk3JZNs-sJVRUpc3B40cow1EQSUEHqNcrVZwdnktF1uV6llRqxRLBq3RjR6w_Cou5mLNLJXvJ_FLA-5WMhayx6ssVZJEgGdVvRD9iIvAkYjPE_KeJZevA8z9lO6jnMdV1nliImDAe8l_NE2Abl3B9glmDDvO2AKrDJk771kVPVOo_ID-f1TFm0TGMQjz9Niatc4UU-70h6FqFd1N5Y-sYaTCorlxdGH7be28aq6nkZm-_QfB0rCupyWnkEEIAn5zL1tGKBxgYjBfaLtRjiTNOhY36duXC89HwpnMSNR0jRoauZgwM8CIYnp2L6IrIYU1P1A5KQd1R4ySNpFKs3vA1d4z6ftdefV2tkuNDY6Q";
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