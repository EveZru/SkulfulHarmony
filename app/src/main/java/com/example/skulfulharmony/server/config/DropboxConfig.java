package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;
    public static final String ACCESS_TOKEN = "sl.u.AF0BBStrWzkNd0dIVk5IbcmMdPtfib99wIevIybxJPBzg0j4LxGlfbFbuSxlOT3ZdOlGsBPqDzNUqXM2RnO1tvXiaI6NHKH08GIf9g3rr3c0u1hp1Ps4p7F_c6OYTIUWs5BxZaEHQHiP2ByP9jDxApc5f_9HWxddRIxbL37zN-QEp18dFeErYsGM4Cx9IV3s9z-K9ZG1QQEThe6v6Nty4E4Cm77zmBmcJod17gU4Wa5P2yOH1FKwpAo1Rg5lQc1xdgkN5kQqpPv7P6nPMUkrz65rON9d9z8k3hlTQGupVt5JORdb8ZmPZ9gUHy5dNZ5BxPsfh8ZlAx5IMINyZ4YTk9dxDFQ2xu6qPp1Tf7qUtFdbP-5VhLmN-uQHhX6VO7Q3OaPmquMtZ7UfA-3Spjcu6KVruvH_AXrKiACkl-KJwlw2gj7JPhl35x8nYaDMVyIjVmJxPLG25v1qAhykFLLf7C-eGYmAjcNvs6Mv6obN6jytB8-zmGawRY9EGGUqeHlXDSHTGlTC-ZSz5sMd2Et10XE9eAfSXGrc9YHu9sx9Ek_xRnc5p5d8yjzN7oVoO2YImR_F7LV9uPMMGXS4QvOt2FyMbBF_cv9RRmnAw4-3xTzTzZoTScD4oyfabpHOpDRTRutgu8DBSepUx8WnQkimcfKEWUruhmuF40wqCq-TZf6o4wFoDYaBP6qw_NUwr3HAvfarKdtu8GptSndWjYivzly-umV1qtBdHXiOflFnsOHDL7bxhVZ_-HevkJzBi0ooegpwOOEWHXHgTmh5jSvFTs6LPVdxPA8Ij_obHRxmi11fnzeWyd6rFBgdvtJgNvY8LgqgNVchOxKNDu6hU-n7bqP0XsocozKtOUsRpMUn3jcp2Cd6WdoAasJar8_ITqCyGfMHwWHtyjSYhD_EIUWWCprh28j564b9jHITirLg6tEGcnxUOQ7gqQkdnTO3SL_SHlu87orzNY7R6XoJ1NBVfBuAT2Nrxz5V1eVzYBEQ4KedkTH0nDpTWloexlieMMXQmSRoY_No7jwWUZ6Pv5B5_S8XJjJQ0wdDvz6xOIe2z-F_Va-oKg79Pn-hPbHz9-PAHpifH3hV49K-HngtLTln5mG0YRMoA1jBlVEMeixZy5oEWptf3bYvgW8KxBMmitcw8Eb6NPIrdo_SP5ODIZUsxJC_jsWD_Dj3JpV1SNgc90Ri5itHsXvkExNhAGlbFo2nTXVobM3ETJfYPYokgiFcUaTqjjqWSfafbYfQoQwJfdAUybI3TxQSB8XuYikNo7HqjDKUbkaII5_eJQK9lkxCPWMJRzVqLyUxxcIz5E2aDbj9V53wbZZjOPykYvlA9z9LiXYe6njogTzGJI98AZRk4P10JZwBFJhYeLSfKVry7fKsnZ_pO91O37NrF90a-4tNYO_f-ib7tPx80MtdiiEnfaJZMVoDilk0pIF-soE2fo3jFQ";

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