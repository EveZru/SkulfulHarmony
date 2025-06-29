package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;
    public static final String ACCESS_TOKEN = "sl.u.AF0L2Ww_PXDmMipG1AtcTZM82JcouNv5zqytAzQ8zgpE3eyXDbm38pcXR9TW2-Sb-bCBis4do40ZF4BGNDefmHKshwoo4xytTa0cxlG4B2a95PHOiz8WZ2bTPPisHPkKg4tkfOJZ0LGM_xvI3zUC0dVGUQuNVyWr7dlm4NVP9IvRdgJYrHrHITGVwhvrg5hVnoEFc72Va4u7GXp2bF3GeXHw4Qa53TMh0iLew6DN-i1KXwaOCIyrDoxvPFKH7vt6RVJ-pF12lHQEb23A1aH7zdNitivBedCX0ZYCFZnxo8NwRMejbMyMorklI5jLxmYQqBQvC_7eFyK2eC2DZe6bW35tsTAq6a8OxxnYAFIe32FN9rEEV2rOhAaajhNRradhEqbRZCmEePiilEQL4HdUlZj9UpGw-TdhZ-uskFArMJuSe4rUaujPYtx151z-07fOgotVOZO4wTF7LrHetKtccvvB54NUDoXCic8clU_VTil5pOSbgd6uCyvRXXIL03YtcwrT9pHYKU_9_bik71VeG7nW2JksylnJq5gqlkpDVLzuEM6WN4lLYTqCdVYFKh90X-cCrILY5EW5hhMrOF9LgvKphtXz66dOJhy5asar2lT8l3U3QIkkDL-Au-nTI2qVUUREvWLZ9WtSpUyOLch8YZfCjOvhRQWg3lbE9CGL9xLDGO_Hd4QhNKlIpLF5naPctCqV1RIDNhPMaP_wjKks73mLcuHj7qyJeuInteakF1bnMZoi-zT3SLNwHvQPJALeNO-Cj3iMDGwy3Wt2Qv7rDGEUcZySyP5cxCpmz2WFOsU_7S2TioQ4ioKYvlkLTacN3RaH3wMmplpb0A9GeNNHE6svtyFZk2aIY9_xFy73uwV0qLztKgkfT_sDCWTq8TWKIMGzKnP2tLDT9CFl1HnFL_NqFJ_T_3qrPviHTeGsF3x5-lgIGcty5T_JGxrfY4Ba4xgKOHM1EWsYbFRpjjMGCAM0TO4SmkmriuS-XAqPYlFQA_zvt7mSGvfPidi79rF8fKv92H2fTbipCyNHezy12kee-C6uqqFXnYXS8MUwk05UKwK5QYNHkY4-W2i0t72IrI29-DbxCJINgSDb-xky6L4DoRJqIKM29qyxeyKrsCq8qKCUz1udo0LNkEpp7RhjIxInQuDWpaX64M_urKgXynLsSdMhgSnYdJAM-bOCu5poviB8iB-4d4he9TbxsI8IC6OOK9ZuGhrhR5cGtie461QxMjPdIevtTnVpZ1sBRoaO7NngPHhH0Ok-jvcNfVDiwqiWYCBLTeIaZAsw4rervJ5ITF8AS1H8ap67ZbTwdv14BeYTUW17PZ6nVtiBHhJCRjwMNLF5H06AeeYiTe-QqxLEpbEBducZq14Bog8L5JGszVfxKEq2DAS1sUsG8OA3ER0IctXD9htBPzJ8RMJLGKj-gQJsrKam-ieVa_YIqhTZgQ";
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