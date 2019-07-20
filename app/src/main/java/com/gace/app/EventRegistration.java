package com.gace.app;

import android.app.Person;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class EventRegistration extends AppCompatActivity {

    EditText name, email, telephone, occcupation, institution, area;
    FirebaseAuth mAuth;
    Button registerforEvent;
    String eventid, sname, semail, stelephone, soccupation, sinstitution,userId, scountry, sregion, sarea;
    ProgressBar loading;
    TextView result;
    FirebaseUser user;
    Spinner country, region;
    String[] the_countries = {"Select Country","Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla",

            "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria",

            "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",

            "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana",

            "Brazil", "British Indian Ocean Territory", "British Virgin Islands", "Brunei", "Bulgaria",

            "Burkina Faso", "Burma (Myanmar)", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde",

            "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island",

            "Cocos (Keeling) Islands", "Colombia", "Comoros", "Cook Islands", "Costa Rica",

            "Croatia", "Cuba", "Cyprus", "Czech Republic", "Democratic Republic of the Congo",

            "Denmark", "Djibouti", "Dominica", "Dominican Republic",

            "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",

            "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Polynesia",

            "Gabon", "Gambia", "Gaza Strip", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece",

            "Greenland", "Grenada", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana",

            "Haiti", "Holy See (Vatican City)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India",

            "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Ivory Coast", "Jamaica",

            "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kosovo", "Kuwait",

            "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein",

            "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia",

            "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mayotte", "Mexico",

            "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco",

            "Mozambique", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia",

            "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea",

            "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama",

            "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn Islands", "Poland",

            "Portugal", "Puerto Rico", "Qatar", "Republic of the Congo", "Romania", "Russia", "Rwanda",

            "Saint Barthelemy", "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia", "Saint Martin",

            "Saint Pierre and Miquelon", "Saint Vincent and the Grenadines", "Samoa", "San Marino",

            "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone",

            "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Korea",
            "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland",
            "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tokelau",
            "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands",
            "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "US Virgin Islands", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam",
            "Wallis and Futuna", "West Bank", "Yemen", "Zambia", "Zimbabwe"};

    String[] ghana_region = {"Select Region","Region not listed","Ahafo Region","Ashanti Region","Bono East Region","Brong Ahafo Region","Central Region"," Eastern Region",
            "Greater Accra Region","North East Region","Northern Region","Oti Region","Savannah Region","Upper East",
            "Upper West Region","Volta Region","Western North Region","Western Region"};

    String[] nigeria_regions = {"Benue" ,"Kogi" ,"Kwara" ,"Nasarawa" ,"Niger" ,"Plateau" ,
            "Federal Capital Territory", "Adamawa" ,"Bauchi" ,"Borno" ,"Gombe" ,"Taraba" ,"Yobe" ,"Jigawa"
            ,"Kaduna" ,"Kano" ,"Katsina" ,"Kebbi" ,"Sokoto" ,"Zamfara", "Abia" ,"Anambra" ,"Ebonyi" ,
            "Enugu" ,"Imo","Akwa Ibom" ,"Bayelsa" ,"Cross River" ,"Rivers" ,"Delta" ,"Edo","Ekiti" ,"Lagos"
            ,"Ogun" ,"Ondo" ,"Osun" ,"Oyo"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registration);
        getSupportActionBar().setTitle("Registration");

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.emil);
        telephone = (EditText) findViewById(R.id.telephone);
        occcupation = (EditText) findViewById(R.id.occupation);
        institution = (EditText) findViewById(R.id.institution);
        result = (TextView) findViewById(R.id.result);
        registerforEvent = (Button) findViewById(R.id.register);
        loading = (ProgressBar) findViewById(R.id.loading);
        country = (Spinner) findViewById(R.id.country);
        region = (Spinner) findViewById(R.id.region);
        area = (EditText) findViewById(R.id.area);
        mAuth = FirebaseAuth.getInstance();

        if(user!=null) {
            try {
                user = mAuth.getCurrentUser();
                email.setText(user.getEmail());
            }catch (NullPointerException e){

            }
        }

        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scountry = the_countries[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                scountry = "Ghana";
            }
        });
        ArrayAdapter<String> county = new ArrayAdapter<String>(EventRegistration.this,android.R.layout.simple_list_item_1,the_countries);
        country.setAdapter(county);

        region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sregion = ghana_region[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sregion = "Greater Accra";
            }
        });
            ArrayAdapter<String> ghana_regionss = new ArrayAdapter<String>(EventRegistration.this,android.R.layout.simple_list_item_1,ghana_region);
            region.setAdapter(ghana_regionss);

        registerforEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //getting the user id
                    userId = user.getUid();

                }catch (NullPointerException e){

                }

                //getting theevent id
                Random random = new Random();
                int number = random.nextInt(999999);
                String eventidhh = "Ev"+number+""+"day";

                eventid = getIntent().getStringExtra("usethis_id");

                //getting others from editText
                sname = name.getText().toString().trim();
                semail = email.getText().toString().trim();
                stelephone = telephone.getText().toString().trim();
                soccupation = occcupation.getText().toString().trim();
                sinstitution = institution.getText().toString().trim();
                sarea = area.getText().toString().trim();
                if(!sname.equals("")){
                    if(!semail.equals("")){
                        if(!stelephone.equals("")){
                            if(!soccupation.equals("")){
                                if(!sinstitution.equals("")){
                                    if(!sarea.equals("")){
                                        loading.setVisibility(View.VISIBLE);

                                        if(uploadtoDatabase()){
                                            loading.setVisibility(View.GONE);
                                            result.setVisibility(View.VISIBLE);
                                            result.setText("Success");
                                        }else{
                                            loading.setVisibility(View.GONE);
                                            result.setVisibility(View.VISIBLE);
                                            result.setTextColor(getResources().getColor(R.color.red));
                                            result.setText("Failure");
                                        }
                                    }else{
                                        area.setError("Required");
                                    }


                                }else{
                                    institution.setError("Required");
                                }
                            }else{
                                occcupation.setError("Required");
                            }
                        }else{
                            telephone.setError("Required");
                        }
                    }else{
                        email.setError("Required");
                    }
                }else{
                    name.setError("Required");
                }

            }
        });

    }


    private boolean uploadtoDatabase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("eventgoers").child(eventid);
        userRef.child("userid").setValue(userId);
        userRef.child("name").setValue(sname);
        userRef.child("email").setValue(semail);
        userRef.child("telephone").setValue(stelephone);
        userRef.child("occupation").setValue(soccupation);
        userRef.child("institution").setValue(sinstitution);
        userRef.child("country").setValue(scountry);
        userRef.child("region").setValue(sregion);
        userRef.child("area").setValue(sarea);
        return true;
    }
}
