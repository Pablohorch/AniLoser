package paquete.horch.aniloser




import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import paquete.horch.aniloser.R

class adaptadorCustomizado(val conList: ArrayList<confesion>): RecyclerView.Adapter<adaptadorCustomizado.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = conList[position].genero+"#"+conList[position].id
        holder.txtTitle.text = conList[position].confesion
        holder.txtdate.text=conList[position].fecha
        holder.txtCat.text=conList[position].categoria

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adaptador, parent, false)
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return conList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtName = itemView.findViewById<TextView>(R.id.txtName)
        val txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)
        val txtdate = itemView.findViewById<TextView>(R.id.fecha)
        val txtCat=itemView.findViewById<TextView>(R.id.categoria)

    }

}