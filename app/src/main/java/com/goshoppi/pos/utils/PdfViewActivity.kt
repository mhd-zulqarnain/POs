package com.goshoppi.pos.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.goshoppi.pos.R
import kotlinx.android.synthetic.main.activity_pdf_view.*
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import timber.log.Timber
import java.io.File


class PdfViewActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener,
    OnPageErrorListener {


    val PDF_DATA = "pdf_intent"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)
       // this.setFinishOnTouchOutside(true);

        val url = getIntent().getStringExtra(PDF_DATA)
        displayFromUri( File(url))

    }
    private fun displayFromUri(uri: File) {
//       val pdfFileName = getFileName(uri)
        Timber.e("$uri")
        pdfView.fromFile(uri)
            .defaultPage(1)
            .onPageChange(this)
            .enableAnnotationRendering(true)
            .onLoad(this)
            .scrollHandle(DefaultScrollHandle(this))
            .spacing(10) // in dp
            .onPageError(this)
            .load()
    }

    /* fun  getFileName( uri:Uri): String? {
        var result:String? = null
        if (uri.getScheme().equals("content")) {
            var cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment()
        }
         return result
     }*/

        override fun onPageChanged(page: Int, pageCount: Int) {
    }

    override fun loadComplete(nbPages: Int) {
        Timber.e("pdf:load completed ${nbPages}")
    }

    override fun onPageError(page: Int, t: Throwable?) {
    Timber.e("pdf:error ${t!!.stackTrace}")
    }


}
