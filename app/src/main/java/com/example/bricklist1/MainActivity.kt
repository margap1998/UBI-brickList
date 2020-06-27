package com.example.bricklist1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.bricklist1.dbhandler.dbManager
import com.example.bricklist1.dbhandler.dbOpener
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

var url=""

class MainActivity : AppCompatActivity() {
    lateinit var dbM:dbManager
    override fun onCreate(savedInstanceState: Bundle?) {
        dbM = dbManager(this)
        super.onCreate(savedInstanceState)
        readSettings()
        listProjects()
        dbM.loadProjects()
        setContentView(R.layout.activity_main)
        addButton.setOnClickListener {
            buttonOnClick()
        }
    }

    private fun buttonOnClick()
    {
        val intent = Intent(this, addProject::class.java)
        startActivity(intent)
        dbM.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        dbM.close()
        writeXMLsettings()
    }

    private fun writeXMLsettings()
    {

        val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuilder.newDocument()
        val rootElement: Element = doc.createElement("BrickListSettings")

        val url: Element = doc.createElement("url")
        url.appendChild(doc.createTextNode(urlStr))
        rootElement.appendChild(url)

        doc.appendChild(rootElement)

        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT,"yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2")

        val path = this.filesDir
        val outDir = File(path,"Output")
        if (!outDir.exists()) outDir.mkdir()
        val file = File(outDir,"settings.xml")
        if (file.exists()) file.delete()
        file.createNewFile()
        transformer.transform(DOMSource(doc), StreamResult(file))
    }

    private fun readSettings() {
        urlStr = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
        try {
            val path = this.filesDir
            val outDir = File(path, "Output")
            if (!outDir.exists()) outDir.mkdir()
            val file = File(outDir, "settings.xml")
            if (file.exists()){
                val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                val doc = docBuilder.parse(file)
                val el = doc.getElementsByTagName("url").item(0).textContent
                urlStr = el
            }
        } catch(e:Exception){
            urlStr = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
        }
    }

    private fun listProjects(){
        for (pr in projectList){
            val a = TextView(this)
            a.text = pr.name
            a.setOnClickListener{
                actualProject = pr
                dbM.updateLastAccess(pr.idProject)
                val intent = Intent(this, projectView::class.java)
                intent.putExtra("nameOfProject",pr.name)
                startActivity(intent)
                dbM.close()
            }
        }
    }
}
