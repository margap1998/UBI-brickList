package com.example.bricklist1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class MainActivity : AppCompatActivity() {
    fun writeXML()
    {
        val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc: Document = docBuilder.newDocument()
        val rootElement: Element = doc.createElement("INVENTORY")


        val lastName: Element = doc.createElement("last-name")
        lastName.appendChild(doc.createTextNode("Doe"))
        rootElement.appendChild(lastName)

        val firstName: Element = doc.createElement("first-name")
        firstName.appendChild(doc.createTextNode("John"))
        rootElement.appendChild(firstName)

        doc.appendChild(rootElement)

        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT,"yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount","2")

        val path = this.filesDir
        val outDir = File(path,"Output")
        outDir.mkdir()
        val file = File(outDir,"test.xml")

        transformer.transform(DOMSource(doc),StreamResult(file))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addButton.setOnClickListener {
            buttonOnClick()
        }
    }
    fun buttonOnClick()
    {
        var ap = addProject()
        val intent = Intent(this, ap.javaClass)
        startActivity(intent)
    }
}
