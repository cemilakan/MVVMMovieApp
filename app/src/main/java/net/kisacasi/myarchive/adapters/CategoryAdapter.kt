package net.kisacasi.myarchive.adapters


import android.annotation.SuppressLint
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_custom_for_rv.view.*
import net.kisacasi.myarchive.MainActivity
import net.kisacasi.myarchive.R
import net.kisacasi.myarchive.models.Genre
import net.kisacasi.myarchive.ui.MovieViewModel
import java.util.*
import kotlin.collections.HashMap

class CategoryAdapter(
    private val categoryList: HashMap<Int, Int>,
    private val isWithPhoto: Boolean
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    lateinit var view: View

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<Genre>() {
        override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {

        view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_custom_for_rv, parent, false)
        return CategoryViewHolder(view)

    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val genre = differ.currentList[position]
        var name = genre.name
        holder.itemView.apply {
            if (categoryList[genre.id] != null) {
                categoryList[genre.id]?.let { movie_category_picture.setImageResource(it) }
            } else {
                val values = categoryList.keys.toIntArray()
                val randomValues = values[Random().nextInt(values.size)]
                movie_category_picture.setImageResource(categoryList[randomValues]!!)
            }
            if (!isWithPhoto) {
                movie_category_picture.visibility = View.GONE
                movie_category_text.textSize = 14F
                movie_category_text.setTextColor(resources.getColor(R.color.colorTextGrey))

            } else {
                name = genre.name.toUpperCase()
            }
//            movie_category_text.setOnClickListener {
//                onCategoriClickListener?.let {
//                    it(genre)
//                }
//            }
            movie_category_text.text = name
            movie_category_root.setOnClickListener {
                onCategoriClickListener?.let {
                    it(genre)
                }
            }
        }

    }

    private var onCategoriClickListener: ((Genre) -> Unit)? = null

    fun setOnCategoryItemClickListener(listener: (Genre) -> Unit) {
        onCategoriClickListener = listener
    }
}