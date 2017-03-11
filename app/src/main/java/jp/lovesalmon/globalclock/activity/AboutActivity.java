package jp.lovesalmon.globalclock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.lovesalmon.globalclock.R;
import jp.lovesalmon.globalclock.databinding.DialogAboutBinding;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DialogAboutBinding binding =
                DataBindingUtil.setContentView(this, R.layout.dialog_about);

        // リンク化対象の文字列、リンク先 URL を指定する

        ArrayList<String> linkWords = new ArrayList<>();
        ArrayList<TextView> tvs = new ArrayList<>();

        linkWords.add(binding.honkot.getText().toString());
        tvs.add(binding.honkot);

        linkWords.add(binding.saaya.getText().toString());
        tvs.add(binding.saaya);

        linkWords.add(binding.creaaa.getText().toString());
        tvs.add(binding.creaaa);

        linkWords.add(binding.yoooo410.getText().toString());
        tvs.add(binding.yoooo410);

        for (int i = 0; i < tvs.size(); i++) {
            String word = linkWords.get(i);
            TextView tv = tvs.get(i);

            Map<String, String> map = new HashMap<String, String>();
            map.put(word, "https://github.com/" + word);

            SpannableString ss = createSpannableString(word, map);
            tv.setText(ss);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private SpannableString createSpannableString(String message, Map<String, String> map) {

        SpannableString ss = new SpannableString(message);

        for (final Map.Entry<String, String> entry : map.entrySet()) {
            int start = 0;
            int end = 0;

            // リンク化対象の文字列の start, end を算出する
            Pattern pattern = Pattern.compile(entry.getKey());
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                start = matcher.start();
                end = matcher.end();
                break;
            }

            // SpannableString にクリックイベント、パラメータをセットする
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    String url = entry.getValue();
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }

        return ss;
    }
}
