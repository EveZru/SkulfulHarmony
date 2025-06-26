package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AF2MFoBnexs8XPt44BVEWi6tTmKNc9d9KQW7PekPmBMY2c7f8ykMxZx0--GKPxOjxOl_yxWOzCvEpLs1WalrH7NksSn-pNAify1F1ZP5tOn2B_qJAHH-0L2loSEw4yWQWJ9jkpm4RGpaAi3hd-K-v72_soSBUWwGJLCY6vxCM984XbxQ3YVh9HIO35H0C_iHI0ji4FJUJjRAZxsAMPysKwk0_YdigEyWm3PbHeEHK0SuGydowToHm5wmFVdQ-d5I9S3vdJnGnO01FHKq0IMC3H9-IjoGajIeMPK64AjLBPXr01A7FjnpiIw_50UfxcSBvuf21dDbHKfFPBGD9E_qk7hS8Lbh1H8Mr_eZ366cguLQ6MZ4G7UvnlTV8i_7XhVUPALea6995I5kmi-S75O49al4MT4ZqQ2h3EiG19Wh5UqwEnPGBCpzAzdvJUZt3YH7v8oloRX4MCt7hVm49WJMcqK2DW1z9Xz6E95lsEWGKjkDf_-aCtm7W4FGEVoTucR6T7vHN3mi0yM_ybgy69XTFMhN6T0lVAudotgdZBT4Qf31Qa5jFCMJPOAR1W60JvpVkrPMUxcA_KzXXnEWsMrmnIAoI0onrFoKiMsiDH5Ho5Mn43RK3jiVPBp2vsfwXzNzPOX4dN1QNforbynLOmsIr_jKvVsBsTxZCoE-yb4CvGpkX8dxjnpdEXDHSJLDyRaDr44uuXiutNGHQvZv2whHhK1Q6AUE095EySYG0rZlJ_yid0-r9v44_lk5EhBuxmnbKNWblNhJgFib-rkMY8_G5Jqrnw0vX4ZLEFAyjcbb2ujSkCZZBijFGK8cqvCYSoDHesk2OYhroWKVMRiaCelhhCJFy1zxmlibKSMFEtUKsebAvRR6fHhHq-oRCjHyvKAwXFyX11t6w-lD8397ZX1NFhga4SslJz2xuqdEM22jRCFCAX5rWmfgL3SXHZr9MjrHIBWOrZpANPacmxBYjgwqM6dY9wdIB9gH_TuyLStSXaQFuq6lTzgubpBaQctwcTcktQFMLVsM98cNIoaxhd6sDd04kBcgtCh_EmWAS2bWzl3ilLMkmB7sSh_3TUb_dpMG49RN6Yv0HsVlzzCgbxCd1L93A8Qsk9ktQJtEEh7OvRMunfIa41Bzhq1n3dZTRAQxuBCWV6bioYJyB50y1E1oLG0OfreiANH0dB-laSZiDQJrwufDTDB_ZflGScv02VbLz41sIhYhGHEAo8enmcg0YKJTVFt8M2lLVdyZrjNmxT68yYFoBu5_f1ZoEbR54CfI-xzJvV4LDOdzfVYgnMHamQ4OKIbxDGNz0FiLbbksrg2TpJudFOuhWZ4_PG84ZDPlEWw4Ci5oVYUKK2UtUE7YdgPx-54HBMXeNzhUOdjVS3OdSX4SgOwOFCh5md0eb4RIDjYbVPK2kM91mbfIY8BC7nKMMDsf8yu7kLs50MdmT4I24Q";
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