package com.example.bricklist1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bricklist1.dbhandler.dbManager
import kotlinx.android.synthetic.main.activity_project_view.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.BufferedOutputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class projectView() : AppCompatActivity() {
    var projName = actualProject.name
    val EXPORT_FILE = 1
    fun showProject(){
        labTV.text = projName
        for (i in 0 until actualProject.partlist.size){
            partsListView.addView(actualProject.partlist[i].createView(this))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_view)
        showProject()
        setting.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        howMany.text = "${actualProject.partlist.size} Elements"
        exportList.setOnClickListener {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
            intent.setType("text/xml")
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_TITLE,"${projName}.xml")
            val chooser = Intent.createChooser(intent,"Choose an app")

            if (intent.resolveActivity(packageManager)!=null){
                startActivity(chooser)
            }
        }
    }


    fun writeXMLexport(data:Uri) {
        val os = this.contentResolver.openOutputStream(data)
        val buf : BufferedOutputStream
        if (os != null) {
            buf = BufferedOutputStream(os)
        }else{
            return
        }
        val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuilder.newDocument()
        val rootElement: Element = doc.createElement("INVENTORY")

        for (item in actualProject.partlist) if (item.found<item.required){
            val iel: Element = doc.createElement("ITEM")

            var el: Element = doc.createElement("itemtype")
            el.appendChild(doc.createTextNode(item.itemtype))
            rootElement.appendChild(el)

            el = doc.createElement("itemid")
            el.appendChild(doc.createTextNode(item.id.toString()))
            rootElement.appendChild(el)

            el = doc.createElement("color")
            el.appendChild(doc.createTextNode(item.colorID.toString()))
            rootElement.appendChild(el)

            el = doc.createElement("qtyfilled")
            el.appendChild(doc.createTextNode((item.required-item.found).toString()))
            rootElement.appendChild(el)

            rootElement.appendChild(iel)
        }
        doc.appendChild(rootElement)

        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT,"yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2")


        transformer.transform(DOMSource(doc),StreamResult(buf))
        buf.flush()
        buf.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && data!=null) {
            if (requestCode == EXPORT_FILE) {
                val d = data.data
                if (d!=null) {
                    writeXMLexport(d)
                }
            }
        }
    }
}
