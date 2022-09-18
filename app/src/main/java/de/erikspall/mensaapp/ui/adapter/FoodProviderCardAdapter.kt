package de.erikspall.mensaapp.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.textview.MaterialTextView
import de.erikspall.mensaapp.R
import de.erikspall.mensaapp.data.sources.local.database.relationships.FoodProviderWithoutMenus
import de.erikspall.mensaapp.domain.usecases.foodprovider.FoodProviderUseCases
import de.erikspall.mensaapp.domain.usecases.foodprovider.GetOpeningHoursAsString
//import de.erikspall.mensaapp.data.sources.local.dummy.DummyDataSource
import de.erikspall.mensaapp.domain.utils.Extensions.getDynamicColorIfAvailable
import de.erikspall.mensaapp.ui.canteenlist.CanteenListFragmentDirections
import javax.inject.Inject

class FoodProviderCardAdapter(
    private val context: Context?,
    private val navController: NavController
) : ListAdapter<FoodProviderWithoutMenus, FoodProviderCardAdapter.FoodProviderViewHolder>(
    FOOD_PROVIDER_COMPARATOR
) {
   // private val dummyList = DummyDataSource.canteens

    class FoodProviderViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {
        val foodProviderImage: ImageView = view!!.findViewById(R.id.image_food_provider)
        val foodProviderTypeChip: Chip = view!!.findViewById(R.id.chip_food_provider_type)
        val foodProviderOpeningInfoText: MaterialTextView = view!!.findViewById(R.id.text_food_provider_opening_info)
        val foodProviderNameText: MaterialTextView = view!!.findViewById(R.id.text_food_provider_name)
        val imageViewTime: ImageView = view!!.findViewById(R.id.image_view_time)
        val container: MaterialCardView = view!!.findViewById(R.id.container_food_provider)
        val containerFoodProviderImage: MaterialCardView = view!!.findViewById(R.id.container_food_provider_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodProviderViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_food_provider, parent, false)
        return FoodProviderViewHolder(adapterLayout)
    }

    @SuppressLint("SetTextI18n") // TODO: Change later
    override fun onBindViewHolder(holder: FoodProviderViewHolder, position: Int) {
       // holder.container.transitionName = holder.container.transitionName + position // make it unique

        val item = getItem(position)

        holder.foodProviderImage.setImageResource(R.drawable.m1)
        holder.foodProviderNameText.text = item.foodProvider.name
        holder.foodProviderTypeChip.text = item.foodProvider.type
        holder.foodProviderOpeningInfoText.text = GetOpeningHoursAsString().invoke(item.openingHours) // TODO : BAD
        /*if (item.openingHours.get(0).opened) {
            holder.foodProviderOpeningInfoText.text = "Noch 2h geöffnet"
        } else {
            val drawableWrap = DrawableCompat.wrap(AppCompatResources.getDrawable(context!!, R.drawable.ic_time)!!).mutate();
            val errorColor = context.getDynamicColorIfAvailable(R.attr.colorError)
            holder.imageViewTime.setColorFilter(errorColor)

            holder.foodProviderOpeningInfoText.text = "Geschlossen!"
            holder.foodProviderOpeningInfoText.setTextColor(errorColor)

        }*/
        holder.container.setOnClickListener {
            //val foodProviderCardDetailName = context!!.getString(R.string.food_provider_card_detail_transition_name)

            //val extras = FragmentNavigatorExtras(holder.container to "container_big")
            val directions = CanteenListFragmentDirections.actionOpenDetails(item.foodProvider.fid.toInt())
            navController.navigate(directions)
        }
    }

    /*override fun getItemCount(): Int {
        return itemCount
    }*/

    companion object {
        private val FOOD_PROVIDER_COMPARATOR = object : DiffUtil.ItemCallback<FoodProviderWithoutMenus>() {
            override fun areItemsTheSame(
                oldItem: FoodProviderWithoutMenus,
                newItem: FoodProviderWithoutMenus
            ): Boolean {
                return oldItem.foodProvider.fid == newItem.foodProvider.fid
            }

            override fun areContentsTheSame(
                oldItem: FoodProviderWithoutMenus,
                newItem: FoodProviderWithoutMenus
            ): Boolean {
                return oldItem.foodProvider == newItem.foodProvider &&
                        oldItem.location == newItem.location &&
                        oldItem.openingHours == newItem.openingHours
            }
        }
    }
}