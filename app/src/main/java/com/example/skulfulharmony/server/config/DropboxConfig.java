package com.example.skulfulharmony.server.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

public class DropboxConfig {

    private DbxClientV2 client;

    // App Key y Access Token como parámetros para crear el cliente
    private static final String APP_KEY = "9lvn2f408xteywp";  // Tu App Key (si la necesitas)
    private static final String APP_SECRET = "rzeneoruadigek";  // Tu App Secret (si la necesitas)
    public static final String ACCESS_TOKEN = "sl.u.AF3znrghQjRSi7WyLgNdLTE-i7_tzxLuAcLZwBMZJPBDGnf38sw9GF183S1lQEHfoDMOFJ1I89QciiIarA_nO6QODX4UDmpQNmY6i_xhadbFhNg7RmqFaERu1sCrq7h1qHb3zgwmQyuvVgSLzZ9kQKztxiL9nF_349yfijIEZ_oxVXbddVNcet4DURKPPW8m_eldBcJk6VZx_5moXGzQm8E3vHDa7eF4Fn04Y43BZK_enil96KwCZ4xlDA9CfnskX0R-TCSrQanK0_VNWzb8WlFea16GOQUzTQFrxCEQsqIqL4rb23O6V0RyTP6jmk5PwCC9YWazwhWG5dAXVCHBDumpYkv0KZ3P8Rv15zUuEjjRKrUh96Zr2VonQuE_XyA1f-Q9HAgBiwadZAHmHfldbAHiT02xtRrhtFH17gQnYcAnLmUtQ_Y37cYbIAufvfRJwisQuAKYSl8UDvH04cPfeMIVlosSiV8g59XHIBegLblnZ2LbWO5T1_fTNdw_S4iYgrNNjhQgROrDEktGYoDkDqnrsHAJOKZO8YVkzj8JPcxPYA_7YVf_K6PMztKjKnN37PHKz6UHS1WL69ilpJ3UbVwS7GBGSr_-wdHRQdv-66oSOIJb1OQX4wYPrsF8s_iysmyPn-cgfeFu9r2jlGSU5erddMqXJ4UV1PT2MGWk9mpk9_pjw1b6oO0QB6U6PzRj64quXpnEFvbJj8Od0wEz0jITtxQe1rKTPyUynWm2RAYufKRxq_3xsZ9PAvDvIwYcmyVboXhNhtiPPFO83XDJlg1kduTRQ-_jH_n-GyEYO-XRqUxMkr-mKioAMlVhR-17B8PZ1wud7jxozbHTALJ4-3X9LArG3sjuvhxHplJvN60pWR-vZnkPUunA0pT4AzKgn7iP-66LK-1cfxi0StVMI0QvhG2HiIVpFXiHejzb5iGTqXGOkozCuuazI7LRlTCqpOPCN1Gn4Qb5lSvZbyzBA03et_fRqwmSIImSSLjCcFdd8mvSGcZyzF_uXE0jcbazCKkp8cYRc4XDPC2mfVaOzx2UwV4win1zOufXA_Y9m_uqgiP4UZ3FXLaBTNZWJYqZCyLBl2tg8tT_OLtfiZk8A3WG0dTXSLToT2AxGL0M8EVPvJSgTo3kLBmZYL6nOHnsj9iqJBLirhDbhtPbrBlLIiALYGmdksRLxv2R_5PKHZZKY_eXzXZaMQBP-BpOF5f0tR2IaJJD9kSDYfD7t9UyXj343dSlkSjicDkusOKPz2sJcGpb_c2WblITeapIyLAdxZuhnWIa0UuO8Rf8FUoUuhlM3tXIamQCZaP3m6Sd0kIWBTDwC_jt4bwpRQy2k6DKpD9apq3RmRflFD0ov-f3EbtQsPV9IHgUUKTjLiTld4UyZDq3VMdCDzNpqfkppQskULGugCrTya_6uTvAF_8pqILs-WRB-LfO0CqQ1cLUfhARQA";

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