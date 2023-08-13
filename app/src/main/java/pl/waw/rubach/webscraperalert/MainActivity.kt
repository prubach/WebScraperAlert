package pl.waw.rubach.webscraperalert

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import pl.waw.rubach.webscraperalert.databinding.ActivityMainBinding
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.id.kursy)

        //setSupportActionBar(binding.toolbar)

        //val navController = findNavController(R.id.kursy)

        //appBarConfiguration = AppBarConfiguration(navController.graph)
        //setupActionBarWithNavController(navController, appBarConfiguration)

        notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder = Notification.Builder(this, channelId)
            //.setContent(contentView)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.ic_launcher_background))
            .setContentIntent(pendingIntent)

        binding.fab.setOnClickListener { view ->
            set_buy_sell()
//            Snackbar.make(view, "Intraco buy: " + get_exchange_rate(), Snackbar.LENGTH_LONG)
//                .setAnchorView(R.id.fab)
//                .setAction("Action", null).show()
        }
    }

    fun set_buy_sell() {
        binding.txtNumIntracoKupno.setText(get_exchange_rate()[0].toString())
        binding.txtNumIntracoSell.setText(get_exchange_rate()[1].toString())

        if (get_exchange_rate()[0]>4.42f) {
            notificationManager.notify(1234, builder.build())
//            val CHANNEL_ID = 46
//            var builder = NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("Sprawdz kantor")
//                .setContentText("Kurs kupna: " + get_exchange_rate()[0])
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }
    }

    fun get_exchange_rate(): FloatArray {
        val py = Python.getInstance()
        val module = py.getModule("kantor")
        try {
            val ret_intraco = module.callAttr("read_intraco")
                //findViewById<EditText>(R.id.etX).text.toString(),
                //findViewById<EditText>(R.id.etY).text.toString())
                .toJava(FloatArray::class.java)
            //val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            //findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)
            return ret_intraco
        } catch (e: PyException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
        return floatArrayOf(0.0f, 0.0f)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.kursy)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}