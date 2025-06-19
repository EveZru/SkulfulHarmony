package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AFypDBj_iCVIcgqCLY_O6N09gJ0WEF6ITjeEEuBYddQ6ZD0kynTv0rYul9Apy2_l5NSIBH9OJPS7ehDw2rxL_Ruq7NrAS_n7vexP0rbwbMmIGDhfmeR56VKoO6Iwq7pu-tZ0ZIOx6Xi1GyRhC749W97YecPureBOSRDwDE1YtZXqf4SmKTF7CpQf9d4FX9F1tmaibhpKx_luqNecyAd9nYDFUwsZW21yy-GHj6YO4TuJO-48TbqPPQ6I7Tbdig9MrT7YaYUOHoiU9nStRfUqudyAucLXxXqiNL7bDCvNEepc-TbqjZUSMm53xWYBscTrpxs_XrUpcx4yzIE2w8QgTd796nN1zKH4qBgWL4lrOfpVYn8EsqLhtBz7ip9gLUtF49BYFSO2OmSLvnJGaFmrIDNMkl825kK_EXlStnjmrU2-3Poi-MrVbm804XBi8nrZbYlmG_KtDJ_X_3zvYGmkjTWQMfWTl5VcsWOXV7vCKoplBQ4qlQ4FesVTwMnnh7TSsd1Di5dteguZHG_wWAOPCWUxjfGpfPOo3p53Cb-0B2FLHAZP2znq4hP1z40QTC-MGhdkwG3qQQaLXjeBe382nNjKh8zYsVhmSNWOFSCSFNks1j8aKx3hOxgResGMHAGdTomg38UcTjEOcgQBzEa4gvLfsZgygOoz7oy3R2jfKM0Ui98kl3jDPhNn082F3zlighm_xB62KOpJrrMuLcOokHNtATOIPron6HmpJqgDnThCKPI4EYK0rfDhqbKon5EH8jPGxUSe_CrCzJkHxQuniHl5SYrP3Pxmd79U0JC-6Qac6X70G_t77MnB5tc1VR-wLD5Oufl2AKHnSa-qjDbEeP-JBSiRoVbK8XhD2hB6YN4zC3wK1KmYHy5pxachpJ8DZpe8YiDlmcFdBFW3nROWx8jGtXU6gT9XvvKgWt4JXb8KrnM22frTdglpaaRqEOni1TL4soafkwseFoxby8fJirTL7uBUBIZHE3SwAzGwoOZLcBkw-zuvzxnt4kYLD4cuQRF_3FVbG_01XpvOCXQ1Isu3URO-m1PqsvG9Vt4Vt08oleGD6K3WNUeIuUZk8eEzmu6EQ0GCfFVUsR5iQSCY0WyBTS6m5TgLWOxvduJ-U7I2kiIh4rCDYZsbIVy6RoM1opxpXUNTCRu5WYqfK6luRy4Yvwh01FZdOFIICQVmrB4tIskTPX7He9xPn4VBI3nPghsDJNlTpeVY52j2BuFvV2oVYdh3qtqFil-WPmpC-EM-rucU_s3jnu5vlEFBeGgS_ILVwk8DIevbFFxR7SJz1CNVGpedmpEL08bzFouEZLnXuUGclM_7QC_TzbCrvIyp6ZEJv5EO74AeWRJVindssWsyAeh4BVoHk7g--IfuSIeV0QLiUv43pPpHkE0FUjNBkMJEMUNUpUvS5saM1p6oqaTrpwZX34nInRnHXrpaV2A_6A";
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