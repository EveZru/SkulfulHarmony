package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AF0wRgLQEgFCs_M22zfs88VaBJqBet-DL0yPDDKFePDPCjj2NZU3hLC5NXY1Lc8vRz-uqE6Jw5sgRxEiIzj-ayR6KZR4aQjGyEnCidnquL4vbJ51CghBAZB9DnxxLgJ0hWFqeD4oiZiI3SoVLSDxqxw8hwz5Ky3haVzGYJmJMF7YCEBjwcq-Wb6GtC1R_Ty3BvnzEeKBhAjcYdFQDDyXlc1-SUB_G_tS2RX7tTylfaw_pfxMLL_CY2bB1Tv-uDyVi8SCMhr18h-HbXukt9d4UaQMh4-PxrgIFv-nLYOJLUgWq_DOEC2T8E0BeUZJGrkd5F1RTy5po3ABZX9F167I586VZWlnVhYtt-3YpO5s1edO-FNMMUbmeIR-yr4S1hVYA7IWsTFMjSLE1-8NXP-N0M0RmZY9U7EYYNFrx8eRFT4qG4HXdVphhMtPFEy73V__NLpBwhTdc31HX1BZeHanFJYKEOknRKpocEv38xxikah5BSM2AUQ426ZiffDkw5hBmnw2kl4XDluMA8Piqs8NXlWzHzzTsGqexGhIRpk1knjcLq1K20gH51uX5gCWWdhMoApIsYB6lG-1FAsDwynK824XLire7Ff3NN4c20karDOlC0Adcbe2_1AI6fJJMIz-Ja2M3a_Z_9REIc5FFvrbdmafT36kI_97RkP6Fws7u5U3pNa-u0moNcPnT2Uh-bs9pi2V7Jh2UecSLrynFtGhfL2exHTqujzFnm7fkreDssg7SaVQaBp3FcGOto0g7m-HnBvDLsv5--TwdJjQpMuxafTzuK6OVDOlQCVzBAd9I-rO1-9V8ObLDp8CsLU0-R_s-WAey0rcg8_vApNzkJoM-_5UGiJ1I721QzI9qs3qM4LegRWDRQrMJB1fzu363JHUQwp2b8ocCb1iS3_NLoIgtPMTSLpKnOCSnEBsy38rWDRX1OsXJqLltXU1JvspQsdDNPWEJKfqmGFgMin9nx77QYq_tvMO-YusCrq7bH8vCjZmrj2Jdg1GaJRmQ2kWfeeLC5g_XmPQlIHXE5qBAQd96VEpGP7N6rtdtv8HIwFeUiLmaMbjg6YsYHplLrw4N7RmSQQBWKNxnvcYycUDbguIzasFG3ryhlvieM202Y4qRUlvqumCKjCK68Ig3PNHZ4nlORyv8fmOIV0YhEC4ZHFmXcqEmztJcx4Oxq12XuhNzvztEUIYMhhFOxmz7luBnmOkZ2PRyawBDkOPhzRyDh7rqLBw9vwGakjgjIPQ-dL_84ek9sY-VDs1DNWu7HZ5_UaiTfDkvaDOyJZBD-3S_GnlOlcYvicgCyqws3_1OKFQLYT3kmKQqgfIVjzi9q2xHYb2y8MuWgeK_WDpSZD0bzhJkx5rGwdYC10KGkv9QGSLsm9s2Ige-gBOZeuaXFX8tX76gj7HXyr4EQFurjWaEvu5ju3Vqazkqy0GBZa_bdr5kVWtSQ";

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