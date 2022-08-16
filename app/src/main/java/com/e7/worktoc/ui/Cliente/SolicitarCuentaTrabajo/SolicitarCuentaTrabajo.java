package com.e7.worktoc.ui.Cliente.SolicitarCuentaTrabajo;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.e7.worktoc.R;

public class SolicitarCuentaTrabajo extends AppCompatActivity {

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_cuenta_trabajo);
        webView = (WebView) findViewById(R.id.webview);

        procesarCargarDePaginaEnElActivity();

        cargarArchivosDeUnInputFileDentroDeNuestroActivity();
    }

    private void procesarCargarDePaginaEnElActivity() {
        permitirInteraccionJS();
        cargarPaginaEnNuestroActivity();
        obligarACargarTodosLinkDentroDeNuestraApp();
    }

    private void permitirInteraccionJS() {
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void cargarPaginaEnNuestroActivity() {
        webView.loadUrl("http://35.174.185.92/workToc/public/solicitar_cuenta/2");
    }

    private void obligarACargarTodosLinkDentroDeNuestraApp() {
        webView.setWebViewClient((new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        }));
    }

    private void cargarArchivosDeUnInputFileDentroDeNuestroActivity() {
        webView.setWebChromeClient(new WebChromeClient() {
            //For Android API >= 21 (5.0 OS)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openPDFChooserActivity();
                return true;
            }

        });
    }

    //aca abrimos los archivos del celular
    //Cuando el usuario ha seleccionado el archivo, se llama al método onActivityResult
    // y reescribimos y esperamos la devolución de llamada.
    private void openPDFChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("application/pdf");
        startActivityForResult(Intent.createChooser(i, "PDF"), FILE_CHOOSER_RESULT_CODE);
    }

    //onActivityResult se utiliza para informar a los usuarios de H5 de la dirección de archivo que eligen.
    // En este método, usando el objeto ValueCallback que guardamos antes y llamando al método onReceiveValue,
    // H5 puede recibir la información de dirección que le pasamos.
    // este metodo sirve para obtener el  Uri del archivo seleccionado por el usuario
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    //aca pasamos el Uri a html5
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

}
