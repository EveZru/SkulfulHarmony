package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFydavKsUneRIAkdFxJpxa4qCTEPz4zTb2O0XIq3Xd4XvsqRzl-cvdsGvbLdbIxroao0L8BVhXwVgEdVIv0P6EKRBE5M5Uzwv09IRO3VYlGkygrRINsQjxd7c0eGx6pzaXSv5lSw3Rl4xqmJlrtzEl6_xycJxElQVN1RZ3PbBz0euyMeS4oEHIvVVFNQlHq6qZ9DteW9hjmYcIHhzrQtOGjLDiJZVV6fhBhTYyrp3rncqde0H9YJDknXyhub0KMiEaRYsozkHSlnBTnZEgmP5n9w-tnAnm3PRNkGWMAMYJU2Cj1fzhs3tsQ58C6GXMsl52SpMxPdFXEOqC6_GkUnCH8y4EqGynbnKEbTcUFxfFLCXsWj6KgDBDbzbze_H5Y8jqlWFn7BxWXe_DFC74_rdztJaxLCgqI_gwzpptRwNSEv5ryy1Cl1JJllFtJKRs0aSQ1J7qWaH7KpYqGR6AjgHWTnwoJtMXGmDhn7iSUGWq4tyn5CQkwQDp491TAaYMkKs_sP2VOfRkLOrxNL0QAZApmnQAXw7c1oNLtduQVzbhOya8DUX_Hj0cxp3E6T4vGs0DYAKpZ3RcyePsmHHV-EsNSKxBRO1ey9f4_HHilBkNJTP4DFiX0CC82R4mvcIZuer2HPYsm7rwI5KnB2vK8x89UwwLCu4k6DA8ugk1fNcNUeZrGRqHPgjd5f1wNYOFFeLZ4kJidbFN61jH-5ICY_ZbLD25oE-k9byWfIdLuW2aU9f-eQxySmvxru06UZbpfJus0bRE6d0Kqrp3lNHsePBuP7xx-IuVLvIAEPm0NAe-jp3dUa7eObgwnopjzTeKWWmKFq8qhKJVPvKanO2b-r6uicCa-21j38wwQzu8ZGYdmGZTIQsppeKmAH_awtck0SQKwMu46m-O0FbZMuF5C5WpCGNsDg_7jOhGwsQapc8RwzUFEXe64DafXXanHzLgAa9UKaFnmRp8shcn8o8Lz35vYMxxgVe_krzemoLU-RBzKgtXWupgle3zCLUxiaYq8LVXNn7SxjMQa1gLYzp0DCSGu8PyoCXNvSnhPhi05bngCGNxvE6_RSHVKbuJXZV5HwzaIzxv0qpi9zjWxJMOAp8AJ9w-c2E7sbzeEVPYaX8AP4_9feLHbL-yOaSsPuf_f8Nprg1fb3Cc0huamyi2d4uCaap9mY9cgn-RusxAa2ZCRMjquE4Z2R2nhTYFWMUNK65Sqg3vNQCryi7SRHe_FUn1niUQI71XuF4hlSpHtufFQuQUhOf3dPAzaWZUtuMl5fZIWIKJDZfjC_qbj-f-mZkEepNuTHby1vkL8WF9uor4ewqFNPNMgdh-vVtRNk3gkQlXXwWBWcUXs8oRaQackZ4zSQuFNShHLxnF_Kz5djUIihE1abS4WLUrWgihBtHpetehzEkYDBYppwLKKUNAE645o726OpMZK7vAa3Mlz0FxJhyQ";
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