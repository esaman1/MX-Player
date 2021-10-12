package hdvideoplayerallformat.gallery.videoplayer.Activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import hdvideoplayerallformat.gallery.videoplayer.R;
import hdvideoplayerallformat.gallery.videoplayer.databinding.ActivityPolicyBinding;

public class PolicyActivity extends AppCompatActivity {

    ActivityPolicyBinding policyBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        policyBinding = DataBindingUtil.setContentView(PolicyActivity.this, R.layout.activity_policy);


        policyBinding.mWebView.setWebViewClient(new MyWebViewClient());
        openURL();
        policyBinding.mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void openURL() {
        policyBinding.mWebView.loadUrl("https://codeveops.blogspot.com/2020/12/privacy-policy.html");
        policyBinding.mWebView.requestFocus();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}