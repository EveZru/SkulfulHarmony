package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFqeDvp_JKR9GLx7kywtI0eUGP8JyMZ2irluDz2eHqc4at1DctebmYJRuAo_UW-P-sIVwg1zeG-XmMcgnqwkYIZ7hd1u3XkRZZNVEUEMv_OJrquA3kyeqMwdyZBc0xq-dr1LLsORke3HkNvji32DjYFi57ggFwfTonvCKsOnuyTY-vfEl2z-6EfpyYfoAtIFTt3AbbUlAdk48l9jtQnutAiXpOnPkZtt_SO0S_gRiiu3pnSPH1aCGKiRqWGb6uax_hCgeVqF4d187dVkOm9V3YMw5NOrr0Ir8WROrNNrTL0JK92Cb-XMnjYlSPRNelOXjMpHOyCxrg0LGK8IN6K8GoTl8JoLN1-GdfdMaOpdf-fj4VzagzOxRGhFzf7LR0ILiKxYMb-A4oO80Ms0s2ellH443X_lG1wB9lW_V79m6LFo9jT7ZhezxXedGntqsDUcQmN5sxxAWFSAhZUvMrUP-UaQEeWGf6vYGzMyXhF4UhpIpSCCFCasVT95ACuoqJY1Khww2D0E1KJ1gwr5OpZwI_J3Uab0o88nUqKfP7RLbWzSIyY57YVXaGaNvOs02b0BuPyy0hVku5E5DqksytSJkGp4-e5tIJWDWgGardM5XhG1F_V4HS2UnzEb37slvZQ9SB1rdIqNvb8II7HFyzuzPGZWMUDP2mcpodGEW81L8iw9bQBAMOhdehb2xf5jZrjzHz0TzImHChOxHbCdqxQtcj46cd3AYHZ52ENIrXFbRtrsFsCIXHYb35SYXiEDo5VaCQzKoCTzCpJdVsUH3xWmOJyWehwTQdkUAjraBkZnQKJPORDVLo7ISHhLfwdIdrVQQn23jVgzlHPeUN3KtZthYZlyjCrVj-ILsrvUhyZfgJk8stuVjJ4tlv1gOc8XjbLsOiJX_DUIVYVmv0FeISduwTQv5WRk5GxWqxmE-UpM5Nq1rTAWslt4d4NAAurnM6oszOehtU3rltsiP8ZHZsKLYDPAh3jYUYTVSyjqXeqxQjhPR6NWl-teo68mApW1hw095WEqLNns4jCwhC6W2bb5_-uK_t3UlTC2crTgnkRGZVxtrmP65iOAIXG0_37pUqSsZDTTzpNAMAs1jBEHq2rxYFcAsmeAOV9-8nKlkbS0lZyDMRuXaG7pv0Em-pvSqUZXo_6PQj_8LH3DOFdTISznVLBBsq1F70JZSAXmw6sFF5-s_Pr9ty8U_zPg_KRiRg-meJ2hN50OXg-qjSsP2DILwl4tOF94uVyjwsA_z2yqtrdosTCdY0xtadNAROzV_pI89LkctL_AB7sbrFLNuYzl0hR_krLLmJvfGhqiJlwx_7oLqzmmrIyfZslilUiPWtcu8y4LxRh1ZWM2RvuZr6XNMzjt9a2wLNBfwQHwMfMPMP9M4R6wyeRWuUCu4zdpRPRN3CR0mCqLlgatMAx_ZKqMEAI-nsPBU5wKUIeSxa-G4YHiCw";
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