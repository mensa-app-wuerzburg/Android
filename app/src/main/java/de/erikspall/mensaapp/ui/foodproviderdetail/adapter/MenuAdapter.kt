package de.erikspall.mensaapp.ui.foodproviderdetail.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import de.erikspall.mensaapp.R
import de.erikspall.mensaapp.domain.const.MaterialSizes
import de.erikspall.mensaapp.domain.enums.AdditiveType
import de.erikspall.mensaapp.domain.enums.Role
import de.erikspall.mensaapp.domain.model.Menu
import de.erikspall.mensaapp.domain.utils.Extensions.getDynamicColorIfAvailable
import de.erikspall.mensaapp.domain.utils.Extensions.pushContentUpBy
import kotlinx.coroutines.*
import java.util.*

class MenuAdapter(
    private val context: Context,
    private val menusHolder: ConstraintLayout,
    var warningsEnabled: Boolean,
    var role: Role,
    private val lifecycleScope: CoroutineScope,
    val onFinishedConstructing: () -> Unit

) : ListAdapter<Menu, MenuAdapter.MenuViewHolder>(
    MENU_COMPARATOR
) {

    class MenuViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val layoutMenus: LinearLayout = view!!.findViewById(R.id.linear_layout_menus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val adapterLayout =
            LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n") // TODO: Change later
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        Log.d("MenuAdapter", "Binding: ${getItem(position).date}")
        holder.layoutMenus.removeAllViews()
        // For each menu a coroutine populates the viewholder
        lifecycleScope.launch {
            Log.d("BindingMenu", "${getItem(position).date} has ${getItem(position).meals.size} meals")
            for (meal in getItem(position).meals) {
                val mealViewHolder = LayoutInflater.from(context)
                    .inflate(R.layout.item_meal, holder.layoutMenus, false)

                //if (mealViewHolder.allViews.count() == 0) { // TODO: Views are already present
                    lateinit var textMealName: MaterialTextView
                    lateinit var textMealPrice: MaterialTextView
                    lateinit var warningIcon: AppCompatImageView
                    lateinit var chipMealCategory: Chip
                    lateinit var layout: ConstraintLayout
                    lateinit var buttonExpand: MaterialButton
                    lateinit var containerAllergenic: LinearLayout
                    lateinit var chipGroupAllergenic: ChipGroup

                    withContext(Dispatchers.IO) {
                        textMealName = mealViewHolder.findViewById(R.id.text_meal_name)
                        textMealPrice = mealViewHolder.findViewById(R.id.text_meal_price)

                        warningIcon = mealViewHolder.findViewById(R.id.image_meal_error)

                        chipMealCategory = mealViewHolder.findViewById(R.id.chip_meal_category)

                        layout = mealViewHolder.findViewById(R.id.layout_menu)

                        buttonExpand = mealViewHolder.findViewById(R.id.button_expand_meal)

                        containerAllergenic =
                            mealViewHolder.findViewById(R.id.container_allergenics)

                        chipGroupAllergenic =
                            mealViewHolder.findViewById(R.id.chip_group_allergenics)
                    }

                    textMealName.text = meal.name
                    textMealPrice.text = meal.prices[role]

                    chipMealCategory.text =
                        meal.additives.filter { it.type == AdditiveType.INGREDIENT }
                            .joinToString { ingredient ->
                                if (ingredient.isNotLiked && warningsEnabled) {
                                    warningIcon.visibility = View.VISIBLE
                                }

                                /* return */ ingredient.name
                            }

                    // TODO: Hide mealCategory if none set

                    meal.additives.filter { it.type == AdditiveType.ALLERGEN }.forEach { allergen ->
                        Chip(context).apply {
                            text = allergen.name
                            setEnsureMinTouchTargetSize(false)
                            isClickable = false
                            if (allergen.isNotLiked && warningsEnabled) {
                                chipIcon = ContextCompat.getDrawable(context, R.drawable.ic_info)
                                val colorError =
                                    context.getDynamicColorIfAvailable(R.attr.colorError)
                                chipIconTint = ColorStateList.valueOf(colorError)
                                setTextColor(colorError)
                                warningIcon.visibility = View.VISIBLE
                            }
                            chipGroupAllergenic.addView(this)
                        }
                    }

                    buttonExpand.setOnClickListener { button ->
                        val v =
                            if (containerAllergenic.visibility == View.GONE) View.VISIBLE else View.GONE
                        TransitionManager.beginDelayedTransition(menusHolder, AutoTransition())
                        containerAllergenic.visibility = v

                        if (v == View.VISIBLE) {
                            button.animate().rotationBy(-180f).apply {
                                duration = 100
                                interpolator = AccelerateInterpolator()
                                withEndAction {
                                    button.rotation = 180f
                                }
                            }
                        } else {
                            button.animate().rotationBy(180f).apply {
                                duration = 100
                                interpolator = AccelerateInterpolator()
                                withEndAction {
                                    button.rotation = 0f
                                }
                            }
                        }
                    }
                    Log.d("BindingMenu", "adding to layoutMenus ...")
                    holder.layoutMenus.addView(mealViewHolder)
                }
                holder.layoutMenus.pushContentUpBy(dp = MaterialSizes.BOTTOM_NAV_HEIGHT)
                if ((itemCount - 1) == position){
                    onFinishedConstructing()
                }

           // }
        }


    }

    companion object {
        private val MENU_COMPARATOR = object : DiffUtil.ItemCallback<Menu>() {
            override fun areItemsTheSame(
                oldItem: Menu,
                newItem: Menu
            ): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(
                oldItem: Menu,
                newItem: Menu
            ): Boolean {
                return oldItem.date == newItem.date &&
                        oldItem.meals.containsAll(newItem.meals)
            }
        }
    }
}