package com.example.phoneaddressapppro

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION_CODES.P
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.phoneaddressapppro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //1 주소록을 가져오기 위해서 사용자에게 퍼미션을 허용을 했는지 확인
        val status = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS")
        // 허용이 되었으면 실행, 아니라면 실행되면 안됨
        if (status == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "주소록 정보 가져오기 요청이 허락됨", Toast.LENGTH_SHORT).show()
        } else {
            // 퍼미션 요청이 여러개 가능
            Toast.makeText(this, "주소록 정보 가져오기 요청이 거부됨", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>("android.permission.READ_CONTACTS"),
                100
            )
        }
        // 2. 주소록 정보를 요청했을 때, 주소록 정보를 해당 주소록 앱에서 선택된 주소  URI 보내줄 때 받을 콜백 함수
        val requiredLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                //콘텐트 프로바이더를 통해서 데이터를 가져온다.
                // 인텐트 안에 있는 데이터를 가져옴 (첫줄이 제일 중요한 이유. 우리가 URI를 가져와야하기 때문)
                val cursor =contentResolver.query(it.data!!.data!!, arrayOf<String>(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER),
                    null,null,null,null)
                // 다음에 갈게 있음.
                    if(cursor!!.moveToNext()){
                        //43 번줄에 있는 순서
                        val name = cursor.getString(0)
                        val phone = cursor.getString(1)
                        binding.textView.text ="name =${name}, phone= ${phone}"
                    }
            }
        }
        //3. 이벤트 처리 ... (주소록 앱 액티비티를 인텐트를 통해서 부른다. 부르고 답을 주는데 주소록을 클릭하면 클린된 주소록의 URI를 돌려준다.)
        // 전체 주소를 원하지 않음. 찍은 그 값만 원함

        binding.button.setOnClickListener {
            // 주소록에서 전화번호 앱  리스트에서 전화번호를 가지고 있는 사람만 리사클러 뷰로 보여줌
            val intent= Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            requiredLauncher.launch(intent)
        }
    }//end of onCreate
}// end of MainActivity