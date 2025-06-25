package com.luizeduardobrandao.letropia.helper

import android.content.Context
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

object BannerAds {

    // IDs de teste do GoogleAdd commentMore actions
    private const val TEST_APP_ID          = "ca-app-pub-3940256099942544~3347511713"
    private const val TEST_BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    /** Inicializa o SDK do AdMob. */
    fun initialize(context: Context) {
        MobileAds.initialize(context) { /* callback opcional */ }
    }

    /**
     * Cria um AdView de banner de teste e adiciona ao container informado.
     * @param context contexto da Activity ou Fragment
     * @param container ViewGroup onde o banner será inserido
     */
    fun loadBanner(context: Context, container: ViewGroup) {
        container.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                container.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // 1) calcula largura disponível em dp
                val metrics = container.context.resources.displayMetrics
                val widthDp = (container.width / metrics.density).toInt()

                // 2) obtém o AdSize adaptativo
                val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, widthDp)

                // 3) converte altura do AdSize de px para int
                val heightPx = adSize.getHeightInPixels(context)

                // 4) atribui essa altura ao FrameLayout (container)
                container.layoutParams = container.layoutParams.apply {
                    height = heightPx
                }
                container.requestLayout()

                // 5) cria e adiciona o AdView normalmente
                val adView = AdView(context).apply {
                    adUnitId = TEST_BANNER_UNIT_ID
                    setAdSize(adSize)
                }
                container.removeAllViews()
                container.addView(adView)

                // 6) carrega o anúncio
                val request = AdRequest.Builder().build()
                adView.loadAd(request)
            }
        })
    }
}