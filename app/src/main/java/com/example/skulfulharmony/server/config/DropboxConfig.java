package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AF1YvHP_ayKvinGH7j8tD-Ry2xiLps2R4clsUVerw-uy-uW09r7xDNaPImQF-NBvHv5eckyBqv6yDmJzMv_EOvuZ-d7V1VnbX_u7Hwbp0XtXxow_bZh7dJjzgGsPnrok-1WjTLDPhg4-DxBYfCraFH-RnxzeAP1f4Bk_wIy7YlWCLUFDkGLvCPUH8O2rB8RyPW_20KztUv718v8_m8Miuoh_1iV76DaXhygza3VNIo6Ao3b472mfcBVvj2uJUnPNrej1se9dTUJWkxLdiPIZtgOj90uL00KCpHgbHg1PFx4jfm6MS63M5dngYe-sgkrncVkR-H4CzHl9mhPgJQhy_-OeGnWqGh_54XnA3MrfsId2IGyX6tdDlVF9Zv5swVUtCd6foxFSNaTCcoGc40usTqzEJnj3CmOsXep_SZDEMXIZBEuv-Kdmmad1sVyqG1IHzBtbQJNbA5A_bQHfFvnrMhCn6G27kQq_VVk41xSYvORTauttGoUsm30kKhtRZlhNNbiiXaN2LJce_JTuzLpDxKe_oX0T23FjYrs4S_E6B74p4xzMVtOOKeB_yFPRciAhGBJmfd4jcCyB_pqxOAfA3jCzr-tFAWj9M-pLAaZ8YD7oQA3Xi0gIu5lpqWwYTfR5oLWmPerFheAJEmzImAyml5Ot0i9UL22lMFvCpHd_2P2lKHrieD9Q8FgFLVZFhWI5wMBGv3RkGm7XEV-vOMAvrItycfT18Zy5kgJOoh-6PpuLy0NAnVYghschFOFnlZHTSPHha7SJ2vzsPqr5fWXpi7OcMMHFHHSokgI4oCpFyQZxzxyZlWmQYbc5SxO-3UhXnBfw8gfeuv-KzjWqXJPbJ4nly_Emdrcrq1_4AuA6rUZl2BwhMXS4JOxx3NdT12xUO0GC5HfsEJ5xnKZM7SHVuHy-c0-v6hysQeYUcJDUS9aXSHz30yH2stYyvkNb0vvXSyUXe9S2mtDaBOIdboPZYTNv5_i68WZgpbwZYMXJ2g-t9FOBH7ZropFkwyTE2fXZb1P80Fj5vAATNpg51fR84a2m53oTERMBQYJB8xqfOxsHFZeBIaxiCdNxMeMlcdZLPEF1OhG3Pf7h0QQds9S0cCbWkbDK4CPkVinrVflW8x-vyVhB2tPumPb844Dx_BEn9VFGgG66J904qEWTHBl1U_0HLvk0aeaCIcbDfkVqYn3CIxuY4aaNZr_-hxEWCn7hCLWGdxCcfWdLcNqgb24Ke0LiHS6JQg4Uq0tSHzWDXfLe9hIfOKMXBOhiek5ZlueenqZzLI3foUJDyyWRcn-fcLMraWHFAi_4KEL0L_pkZJmxEdrzadYT7MNOx23DZtoyQfVc5cDJl_eEbbY8uCfYCrP_tzdvYOZ8AoLsvIRNGU0tZkH8DpYAFPe470irfpHdDw4af89ZanyxbTfS9nYoq9PH3aoLeTJJwo8xeIyr6bHTjA";
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