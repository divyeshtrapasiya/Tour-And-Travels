package com.example.masterproject.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.masterproject.AdminActivity
import com.example.masterproject.DashBoardActivity.Companion.UserAdmin
import com.example.masterproject.Adapter.PakageAdapter
import com.example.masterproject.PakageModel
import com.example.masterproject.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentHomeBinding
    lateinit var reference: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initview()

        return binding.root

    }

    private fun initview() {


        binding.progressbarhome.visibility = View.VISIBLE

        reference = FirebaseDatabase.getInstance().reference


        binding.add.setOnClickListener {


            var i = Intent(context, AdminActivity::class.java)
            startActivity(i)

        }

        if (UserAdmin== 0)
        {
            binding.add.visibility = View.GONE
        }
        else{

            binding.add.visibility = View.VISIBLE
        }

        reference.root.child("PakageTb").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                binding.progressbarhome.visibility=View.VISIBLE

                var datalist: ArrayList<PakageModel> = ArrayList()

                for (data in snapshot.children) {

                    binding.progressbarhome.visibility=View.GONE
                    var pakagedata = data.getValue(PakageModel::class.java)

                    pakagedata?.let { datalist.add(it) }

                }


                var adapter = PakageAdapter(this, datalist)

                var manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.recycleview.layoutManager = manager

                binding.recycleview.adapter = adapter


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

