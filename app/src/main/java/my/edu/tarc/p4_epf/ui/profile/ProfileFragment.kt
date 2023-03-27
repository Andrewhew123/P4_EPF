package my.edu.tarc.p4_epf.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import my.edu.tarc.p4_epf.R
import my.edu.tarc.p4_epf.databinding.FragmentProfileBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment(), MenuProvider {

    var _binding : FragmentProfileBinding? = null;
    val binding get() = _binding!!

    lateinit var sharedPref: SharedPreferences


    //Implicit Intent
    private val getPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri->
        if (uri != null) {
            binding.imageViewProfile.setImageURI(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageViewProfile.setOnClickListener {
            getPhoto.launch("image/*")
        }

        //Read Profile Picture
        val image = readProfilePicture()
        if (image == null) {
            binding.imageViewProfile.setImageResource(R.drawable.baseline_account_box_24)
        }else {
            binding.imageViewProfile.setImageBitmap(image)
        }

        //Read Profile Info
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        val name = sharedPref.getString(getString(R.string.name), getString(R.string.nav_header_title))
        val email = sharedPref.getString(getString(R.string.email), getString(R.string.nav_header_subtitle))

        binding.editTextName.setText(name)
        binding.editTextEmailAddress.setText(email)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        //Add support to Menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.profile_menu, menu)
        menu.findItem(R.id.action_about).isVisible = false
        menu.findItem(R.id.action_settings).isVisible = false
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.action_save){

            //TODO: Save profile info
            saveProfilePicture(binding.imageViewProfile)

            //Save profile info
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmailAddress.text.toString()

            with(sharedPref.edit()){
                putString(getString(R.string.name), name)
                putString(getString(R.string.email), email)
                apply()
            }

            Toast.makeText(requireContext(), getString(R.string.profile_save), Toast.LENGTH_SHORT).show()

        }else if (menuItem.itemId == android.R.id.home) {
            //Handling the Up button
            findNavController().navigateUp()
        }
        return true
    }


    private fun saveProfilePicture(view: View) {
        val filename = "profile.png"
        val file = File(this.context?.filesDir, filename)
        val image = view as ImageView

        val bd = image.drawable as BitmapDrawable
        val bitmap = bd.bitmap
        val outputStream: OutputStream

        try{
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            outputStream.flush()
            outputStream.close()
        }catch (e: FileNotFoundException){
            e.printStackTrace()
        }
    }

    private fun readProfilePicture(): Bitmap? {
        val filename = "profile.png"
        val file = File(this.context?.filesDir, filename)

        if(file.isFile){
            try{
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                return bitmap
            }catch (e: FileNotFoundException){
                e.printStackTrace()
            }
        }
        return null
    }



}