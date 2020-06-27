package com.example.bricklist1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bricklist1.dbhandler.dbManager
import com.example.bricklist1.model.project
import kotlinx.android.synthetic.main.activity_add_project.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory


class addProject : AppCompatActivity() {
    val dbM = dbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_project)
        addProButton.setOnClickListener {
            addProject()
        }
    }

    fun addProject(){
        val url = "urlStr${idEdit.text.toString()}.xml"
        val name = nameEdit.text.toString()
        val p =readProjectFromXML(url,name)
        if (p!=null){
            dbM.insertNewInventory(p)
            dbM.close()
            actualProject = p
            projectList.add(p)
            val intent = Intent(this, projectView::class.java)
            intent.putExtra("nameOfProject",p.name)
            startActivity(intent)
        }
    }


    fun readProjectFromXML(urlS:String,name:String): project?{
        var res: project? = null
        try {
            val url = URL(urlS)
            val connection: HttpURLConnection = url
                .openConnection() as HttpURLConnection
            connection.setDoInput(true)
            connection.connect()
            val input: InputStream = connection.getInputStream()
            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc = docBuilder.parse(input)
            res = project(name)
            res.active = 1
            val iLen = doc.getElementsByTagName("ITEM").length

            val nl = doc.getElementsByTagName("ALTERNATE")
            val il = doc.getElementsByTagName("ITEMTYPE")
            val cl = doc.getElementsByTagName("ITEMID")
            val ql = doc.getElementsByTagName("QTY")
            val coll = doc.getElementsByTagName("COLOR")
            val el = doc.getElementsByTagName("EXTRA")



            for (i in 1..iLen) if(nl.item(i).textContent =="N"){
                val colorID = coll.item(i).textContent.toInt()
                val itemtype = il.item(i).textContent
                val required = ql.item(i).textContent.toInt()
                val code = cl.item(i).textContent
                val extra = if(el.item(i).textContent =="N") 0 else 1
                val part = dbM.getPartFromXML(code,colorID,itemtype,required,extra)
                if (part!=null) res.partlist.add(part)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            res = null
        }
        return res
    }

    override fun onDestroy() {
        super.onDestroy()
        dbM.close()
    }
}