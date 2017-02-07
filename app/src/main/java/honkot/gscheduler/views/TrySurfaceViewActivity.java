/**
XMLレイアウト内に埋め込んだ拡張したSurfaceViewを使ってフリックすると画像がスライドする様にする。
SurfaceView以外はタッチイベント処理をしたくないので、SurfaceViewに対してタッチイベントを埋め込む。
SurfaceViewをフリックした強さによって速さをかえてだんだんスライドするスピードが遅くなる様にする。
なんかフレームレートが安定しない。やっぱりOpenGLを使った方がいいのかもしれない。

大事なところ
　main.xmlに追加したSurfaceViewをカスタマイズしたSurfaceViewに置き換える
　　例えばtrial.sample.trysurfaceview01.SurfaceViewExtの様に。
　カスタマイズしたSurfaceViewのコンストラクタは3種類とも用意する。
　画像のファイル名に大文字は使えない trySurfaceView00GB.pngとかはだめ
　　try_surface_view_00_bg.pngとかにする(小文字の0-9とa-zに_.だけ使える)

検索した事
　android SurfaceView 画像を表示する
　android 親のレイアウト サイズ
　android SurfaceView Bitblt
　定期的に更新 Android
　Runnable Android
　Android ゲームループ

開発環境
　Eclipse IDE バージョン: 3.7 Indigo Service Release 2
　ターゲットプラットフォーム: 2.1
　API レベル: 7

 */

package honkot.gscheduler.views;

import android.app.Activity;
import android.os.Bundle;

import honkot.gscheduler.R;

public class TrySurfaceViewActivity extends Activity {
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
























