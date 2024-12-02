package com.example.eggenda.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.activity.enableEdgeToEdge
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eggenda.R
import com.example.eggenda.databinding.FragmentGalleryBinding
import com.example.eggenda.gameMonsterChoose.GameMonsterDialogFragment
import com.example.eggenda.gamePetChoose.GamePetChooseViewModel
import com.example.eggenda.gamePetChoose.PetChooseDialogFragment
import com.example.eggenda.gamePetChoose.SharedPreferenceManager
import com.example.eggenda.gamePlay.petInfo2
import com.example.eggenda.gamePlay.stageInfo
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.input.ObservableInputStream

class GalleryFragment : Fragment() {

    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private lateinit var petInfo: petInfo2
    private lateinit var galleryViewModel: GalleryViewModel
    private lateinit var allPetsArrayID : IntArray
    private lateinit var petsAdapter: GalleryAdapter
    private lateinit var monsterAdapter : GalleryMonsterAdapter
    private lateinit var characterRecyclerView: RecyclerView
    private lateinit var allStageArrayID : IntArray
    private lateinit var stageInfo: stageInfo
    private lateinit var pet_view_button : View
    private lateinit var monster_view_button:View
    private lateinit var viewSwitcher : ViewSwitcher
    private lateinit var petChosenImage : ImageView
    private lateinit var petChoosenText : TextView
    private lateinit var gallery_pet_invisible : View

    private var _binding: FragmentGalleryBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.edrqwe
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //inflate the layout for the gallery fragment
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        sharedPreferenceManager = SharedPreferenceManager(requireContext())
        petInfo = petInfo2()
        stageInfo = stageInfo()

        val binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        characterRecyclerView = binding.galleryCharacterchooseRecyclerView

        pet_view_button = root.findViewById(R.id.gallery_pet_button)
        monster_view_button = root.findViewById(R.id.gallery_monster_button)

        characterRecyclerView = root.findViewById(R.id.gallery_characterchoose_recyclerView)
        characterRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        galleryViewModel = ViewModelProvider(this)[GalleryViewModel::class.java]
        //initalized classes and views
        characterRecyclerView = root.findViewById(R.id.gallery_characterchoose_recyclerView)

        monster_view_button.setBackgroundResource(R.drawable.gallery_switch_background)
        pet_view_button.setBackgroundResource(R.drawable.game_dialog_btn_norm)
        viewSwitcher  = root.findViewById(R.id.viewSwitcher)

        petChosenImage = root.findViewById(R.id.game_pet_choose_image)
        petChoosenText = root.findViewById(R.id.gallery_pet_name)
        gallery_pet_invisible = root.findViewById(R.id.gallery_pet_invisible)
        val textTitleSetting : TextView= root.findViewById(R.id.gallery_pet_name)
        val box_title : TextView = root.findViewById(R.id.gallery_title)

        //initialize pets array that has in the code in int array

        allPetsArrayID = IntArray(petInfo.getTotalPetAmount()) { it }
        allStageArrayID = IntArray(stageInfo.stageTotalNum){it}


        //initalize it as pet first
        gallery_pet_invisible.visibility = View.VISIBLE
        petViewButton()
        // Make sure both views are clickable
        pet_view_button.isClickable = true
        monster_view_button.isClickable = true

        pet_view_button.setOnClickListener {
            textTitleSetting.text = "Please Select a Pet!"
            box_title.text = "Pet Gallery"
            gallery_pet_invisible.visibility = View.VISIBLE
            petViewButton()
        } // end of pets view click listener

        monster_view_button.setOnClickListener {
            textTitleSetting.text = "Please Select a Monster!"
            box_title.text = "Monster Gallery"
            gallery_pet_invisible.visibility = View.GONE
            monsterViewButton()
        } // end of pets view click listener

        return root
    }

    private fun petViewButton(){
        monster_view_button.setBackgroundResource(R.drawable.gallery_switch_background)
        pet_view_button.setBackgroundResource(R.drawable.game_dialog_btn_norm)
        galleryViewModel.clearSelectionMonster()

        //set the adapter to show the pets
        petsAdapter = GalleryAdapter(
            allPetsArrayID,
            sharedPreferenceManager,
            {petId -> galleryViewModel.selectPet(petId)},
            {petId -> showPetDetailDialog(petId)})

        petsAdapter.notifyDataSetChanged()
        characterRecyclerView.adapter = petsAdapter



        //variables to set
        viewSwitcher.setDisplayedChild(1) //preset the format
        galleryViewModel.currentSelectedPet.observe(viewLifecycleOwner){ petId ->

            //set the chosen image
            if (petId == null) {
                // No pet selected, return to the initalized format
                viewSwitcher.setDisplayedChild(1)
                petChosenImage.setOnLongClickListener(null)
            } else {
                // Pet selected, display the image
                viewSwitcher.setDisplayedChild(0)
                petChosenImage.setImageResource(petInfo.getPetInfoById(petId)?.imageId ?: R.layout.gallery_pet_items_frame)
                petChoosenText.text = petInfo.getPetInfoById(petId)?.name
                petChosenImage.visibility = View.VISIBLE

                //set a long click so it can show the info of the pet
                petChosenImage.setOnLongClickListener{
                    showPetDetailDialog(petId)
                    true
                }
            }

        }
    }

    private fun monsterViewButton(){
        Log.d("GalleryFragment", "Monster button clicked")
        monster_view_button.setBackgroundResource(R.drawable.game_dialog_btn_chosen)
        pet_view_button.setBackgroundResource(R.drawable.gallery_switch_background)
        galleryViewModel.clearSelectionPets()

        //set the adapter to show the pets
        monsterAdapter = GalleryMonsterAdapter(allStageArrayID,
            sharedPreferenceManager,
            {monsterId -> galleryViewModel.selectMonster(monsterId)},
            {monsterId -> showMonsterDetailDialog(monsterId)})

        monsterAdapter.notifyDataSetChanged()
        characterRecyclerView.adapter = monsterAdapter


        //variables to set
        galleryViewModel.currentSelectedMonster.observe(viewLifecycleOwner){ monsterId ->

            if (monsterId == null) {
                // No pet selected, return to the initalized format
                viewSwitcher.setDisplayedChild(1)
                petChosenImage.setOnLongClickListener(null)
            } else {
                // Pet selected, display the image
                viewSwitcher.setDisplayedChild(0)
                petChosenImage.setImageResource(stageInfo.StageInfoMap(monsterId)?.bossImageId ?: R.layout.gallery_pet_items_frame)
                petChoosenText.text = stageInfo.StageInfoMap(monsterId)?.name
                petChosenImage.visibility = View.VISIBLE

                //set a long click so it can show the info of the pet
                petChoosenText.setOnLongClickListener{
                    showMonsterDetailDialog(monsterId)
                    true
                }
            }

        }
    }

    private fun showPetDetailDialog(petId: Int) {
        // Show a dialog with pet details
        val dialog = PetChooseDialogFragment.newInstance(petId)
        dialog.show(parentFragmentManager, "PetChooseDialog")
    }

    //set the show dialog funciton
    private fun showMonsterDetailDialog(stageId:Int){
        val dialog = GameMonsterDialogFragment.newInstance(stageId)
        dialog.show(parentFragmentManager, "MonsterChooseDialog")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}