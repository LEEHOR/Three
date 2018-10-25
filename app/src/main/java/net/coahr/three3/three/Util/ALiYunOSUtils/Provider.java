package net.coahr.three3.three.Util.ALiYunOSUtils;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.common.OSSConstants;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.utils.IOUtils;

import net.coahr.three3.three.Util.Preferences.PreferencesTool;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Provider extends OSSAuthCredentialsProvider {
    private String mAuthServerUrl;
    private AuthDecoder mDecoder;
    private Context mcontent;
    private PreferencesTool mpreferencesTool;
    public Provider(String authServerUrl, Context context) {
        super(authServerUrl);
        this.mcontent=context;
        this.mAuthServerUrl=authServerUrl;
        mpreferencesTool =new PreferencesTool(context);
    }
    public void setAuthServerUrl(String authServerUrl) {
        this.mAuthServerUrl = authServerUrl;
    }
    public void setDecoder(AuthDecoder decoder) {
        this.mDecoder = decoder;
    }

    @Override
    public OSSFederationToken getFederationToken() throws ClientException {
        OSSFederationToken authToken = null;
        String authData;
        String expirations = mpreferencesTool.getExpiration("expiration");
       Log.e("Provider", "getFederationToken: "+expirations +"/"+System.currentTimeMillis());
        Long aLong=null;
        if (expirations !=null){
            aLong = Long.valueOf(expirations);
          //  Log.e("expirations", "getFederationToken: "+aLong +"/"+System.currentTimeMillis());
            if (System.currentTimeMillis()>aLong){
              //  Log.e("Provider", "getFederationToken: "+ mAuthServerUrl);
                Log.e("Provider", "getFederationToken: "+"重新请求");
                try {
                    URL stsUrl = new URL(mAuthServerUrl);
                    HttpURLConnection conn = (HttpURLConnection) stsUrl.openConnection();
                    conn.setConnectTimeout(10000);
                    InputStream input = conn.getInputStream();
                    authData = IOUtils.readStreamAsString(input, OSSConstants.DEFAULT_CHARSET_NAME);
                    if (mDecoder != null) {
                        authData = mDecoder.decode(authData);
                    }
                    JSONObject jsonObj = new JSONObject(authData);
                    int statusCode = jsonObj.getInt("StatusCode");
                    if (statusCode == 200) {
                        String ak = jsonObj.getString("AccessKeyId");
                        String sk = jsonObj.getString("AccessKeySecret");
                        String token = jsonObj.getString("SecurityToken");
                        String expiration = jsonObj.getString("Expiration");
                        mpreferencesTool.setExpiration("expiration",expiration);
                        mpreferencesTool.setSecurityToken("token",token);
                        mpreferencesTool.setAK("ak",ak);
                        mpreferencesTool.setSK("sk",sk);
                        Log.e("Provider", "getFederationToken1: "+"请求1"+ak+"/"+sk+"/"+token+"/"+expiration );
                        authToken = new OSSFederationToken(ak, sk, token, expiration);
                        return authToken;
                    } else {
                        String errorCode = jsonObj.getString("ErrorCode");
                        String errorMessage = jsonObj.getString("ErrorMessage");
                        throw new ClientException("ErrorCode: " + errorCode + "| ErrorMessage: " + errorMessage);
                    }

                } catch (Exception e) {
                    throw new ClientException(e);
                }
            }else {
                Log.e("Provider", "getFederationToken: "+"拼接");
                String tempAK = mpreferencesTool.getAK("ak");

                String tempSK = mpreferencesTool.getSK("sk");

                String securityToken = mpreferencesTool.getSecurityToken("token");

                Log.e("Provider", "getFederationToken3: "+expirations+"/"+tempAK+"/"+tempSK+"/"+securityToken );
                authToken = new OSSFederationToken(tempAK, tempSK, securityToken, expirations);

                return authToken;
            }
        } else {
            try {
                URL stsUrl = new URL(mAuthServerUrl);
                HttpURLConnection conn = (HttpURLConnection) stsUrl.openConnection();
                conn.setConnectTimeout(10000);
                InputStream input = conn.getInputStream();
                authData = IOUtils.readStreamAsString(input, OSSConstants.DEFAULT_CHARSET_NAME);
                if (mDecoder != null) {
                    authData = mDecoder.decode(authData);
                }
                JSONObject jsonObj = new JSONObject(authData);
                int statusCode = jsonObj.getInt("StatusCode");
                if (statusCode == 200) {
                    String ak = jsonObj.getString("AccessKeyId");
                    String sk = jsonObj.getString("AccessKeySecret");
                    String token = jsonObj.getString("SecurityToken");
                    String expiration = jsonObj.getString("Expiration");
                    mpreferencesTool.setExpiration("expiration",expiration);
                    mpreferencesTool.setSecurityToken("token",token);
                    mpreferencesTool.setAK("ak",ak);
                    mpreferencesTool.setSK("sk",sk);
                    Log.e("Provider", "getFederationToken2: "+"请求2"+ak+"/"+sk+"/"+token+"/"+expiration );
                    authToken = new OSSFederationToken(ak, sk, token, expiration);
                    return authToken;
                } else {
                    String errorCode = jsonObj.getString("ErrorCode");
                    String errorMessage = jsonObj.getString("ErrorMessage");
                    throw new ClientException("ErrorCode: " + errorCode + "| ErrorMessage: " + errorMessage);
                }

            } catch (Exception e) {
                throw new ClientException(e);
            }
        }

    }
}
