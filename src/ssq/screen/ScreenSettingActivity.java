package ssq.screen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class ScreenSettingActivity extends Activity
{
  private static final String ȡ��                         = "ȡ��";
  private static final String ȷ��                         = "ȷ��";
  private static final String ������һ��0_255֮�������_���������Χ_����ȡ = "������һ��0-255֮�������. \n���������Χ, ����ȡ. ";
  private static final String ������ɫ����                     = "������ɫ����";
  private static final String ���������ʽ                    = "���������ʽ";
  private CheckBox            filterEnBox, rgbEnBox, tooletEnBox, relativelyBox;
  private SeekBar             rBar, gBar, bBar, aBar;
  private Button              rButton, gButton, bButton, aButton, resetButton;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.screen_settings);
    initView();
  }
  
  private void initView()
  {
    // findview
    SharedPreferences sharedPreferences = getSharedPreferences(Scruin.SCREEN, MODE_PRIVATE);
    
    filterEnBox = (CheckBox) findViewById(R.id.filter_en_cb);
    rgbEnBox = (CheckBox) findViewById(R.id.rgb_en_cb);
    tooletEnBox = (CheckBox) findViewById(R.id.toolet_en_cb);
    relativelyBox = (CheckBox) findViewById(R.id.relatively_en);
    resetButton = (Button) findViewById(R.id.reset_b);
    rBar = (SeekBar) findViewById(R.id.r_sb);
    gBar = (SeekBar) findViewById(R.id.g_sb);
    bBar = (SeekBar) findViewById(R.id.b_sb);
    aBar = (SeekBar) findViewById(R.id.a_sb);
    rButton = (Button) findViewById(R.id.red_b);
    gButton = (Button) findViewById(R.id.green_b);
    bButton = (Button) findViewById(R.id.blue_b);
    aButton = (Button) findViewById(R.id.alpha_b);
    
    filterEnBox.setChecked(sharedPreferences.getBoolean(Scruin.FILTER_EN, false));
    rgbEnBox.setChecked(sharedPreferences.getBoolean(Scruin.RGB_EN, false));
    tooletEnBox.setChecked(sharedPreferences.getBoolean(Scruin.TOOLET_EN, false));
    //		relativelyBox.setChecked(sharedPreferences.getBoolean(Scruin.RELATIVELY_EN, false));
    
    rBar.setProgress(sharedPreferences.getInt(Scruin.RED, 0));
    gBar.setProgress(sharedPreferences.getInt(Scruin.GREEN, 0));
    bBar.setProgress(sharedPreferences.getInt(Scruin.BLUE, 0));
    aBar.setProgress(sharedPreferences.getInt(Scruin.ALPHA, 0));
    
    if (!filterEnBox.isChecked())
    {
      disableAll();
    }
    else if (!rgbEnBox.isChecked())
    {
      disableColor();
    }
    else
    {
      enableColor();
    }
    
    //		if (!tooletEnBox.isChecked())
    //		{
    //			relativelyBox.setEnabled(false);
    //		}
    
    rButton.setOnClickListener(onClickListener);
    gButton.setOnClickListener(onClickListener);
    bButton.setOnClickListener(onClickListener);
    aButton.setOnClickListener(onClickListener);
    
    rBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    gBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    bBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    aBar.setOnSeekBarChangeListener(onSeekBarChangeListener);
    
    resetButton.setOnClickListener(new OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        rBar.setProgress(0);
        gBar.setProgress(0);
        bBar.setProgress(0);
        aBar.setProgress(0);
        saveData();
        Scruin.refreshToolet();
      }
    });
    
    filterEnBox.setOnCheckedChangeListener(onCheckedCheageListener);
    rgbEnBox.setOnCheckedChangeListener(onCheckedCheageListener);
    tooletEnBox.setOnCheckedChangeListener(onCheckedCheageListener);
    relativelyBox.setOnCheckedChangeListener(onCheckedCheageListener);
    
    relativelyBox.setVisibility(View.GONE);
  }
  
  private OnClickListener         onClickListener         = new OnClickListener()
                                                          {
                                                            @Override
                                                            public void onClick(final View v)
                                                            {
                                                              final EditText editText = new EditText(ScreenSettingActivity.this);
                                                              
                                                              new AlertDialog.Builder(ScreenSettingActivity.this).setTitle(������ɫ����).setMessage(������һ��0_255֮�������_���������Χ_����ȡ).setIcon(android.R.drawable.ic_dialog_info).setView(editText).setPositiveButton(ȷ��, new DialogInterface.OnClickListener()
                                                              {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which)
                                                                {
                                                                  String string = editText.getText().toString();
                                                                  try
                                                                  {
                                                                    switch (v.getId())
                                                                    {
                                                                      case R.id.red_b:
                                                                        ScreenSettingActivity.this.rBar.setProgress((int) (Integer.parseInt(string)));
                                                                        break;
                                                                      
                                                                      case R.id.green_b:
                                                                        ScreenSettingActivity.this.gBar.setProgress((int) (Integer.parseInt(string)));
                                                                        break;
                                                                      case R.id.blue_b:
                                                                        ScreenSettingActivity.this.bBar.setProgress((int) (Integer.parseInt(string)));
                                                                        break;
                                                                      case R.id.alpha_b:
                                                                        ScreenSettingActivity.this.aBar.setProgress((int) (Integer.parseInt(string)));
                                                                        break;
                                                                      default:
                                                                        //																						android.util.Log.i(Context.WINDOW_SERVICE, "" + v.getId());
                                                                        break;
                                                                    }
                                                                  }
                                                                  catch (NumberFormatException e)
                                                                  {
                                                                    Scruin.showToast(���������ʽ);
                                                                    return;
                                                                  }
                                                                  dialog.dismiss();
                                                                }
                                                              }).setNegativeButton(ȡ��, new DialogInterface.OnClickListener()
                                                              {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which)
                                                                {
                                                                  dialog.dismiss();
                                                                }
                                                              }).create().show();
                                                              
                                                              saveData();
                                                              Scruin.refreshToolet();
                                                            }
                                                          };
  
  private OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener()
                                                          {
                                                            @Override
                                                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                                                            {
                                                              saveData();
                                                              Scruin.refreshToolet();
                                                            }
                                                            
                                                            @Override
                                                            public void onStopTrackingTouch(SeekBar seekBar)
                                                            {
                                                            }
                                                            
                                                            @Override
                                                            public void onStartTrackingTouch(SeekBar seekBar)
                                                            {
                                                            }
                                                          };
  private OnCheckedChangeListener onCheckedCheageListener = new OnCheckedChangeListener()
                                                          {
                                                            @Override
                                                            public void onCheckedChanged(final CompoundButton cb, boolean isChecked)
                                                            {
                                                              switch (cb.getId())
                                                              {
                                                                case R.id.filter_en_cb:
                                                                  if (isChecked)
                                                                  {
                                                                    aBar.setEnabled(true);
                                                                    aButton.setEnabled(true);
                                                                    tooletEnBox.setEnabled(true);
                                                                    relativelyBox.setEnabled(true);
                                                                    rgbEnBox.setEnabled(true);
                                                                    
                                                                    if (ScreenSettingActivity.this.rgbEnBox.isChecked())
                                                                    {
                                                                      enableColor();
                                                                    }
                                                                  }
                                                                  else
                                                                  {
                                                                    disableAll();
                                                                  }
                                                                  break;
                                                                
                                                                case R.id.rgb_en_cb:
                                                                  if (isChecked)
                                                                  {
                                                                    enableColor();
                                                                  }
                                                                  else
                                                                  {
                                                                    disableColor();
                                                                  }
                                                                  break;
                                                                case R.id.toolet_en_cb:
                                                                  relativelyBox.setEnabled(isChecked);
                                                                  break;
                                                              }
                                                              
                                                              saveData();
                                                              Scruin.refreshToolet();
                                                            }
                                                          };
  
  private void enableColor()
  {
    rBar.setEnabled(true);
    gBar.setEnabled(true);
    bBar.setEnabled(true);
    
    rButton.setEnabled(true);
    gButton.setEnabled(true);
    bButton.setEnabled(true);
    
    if (aBar.getProgress() <= 20)
    {
      aBar.setProgress(20);
    }
  }
  
  protected void saveData()
  {
    Editor editor = getSharedPreferences(Scruin.SCREEN, MODE_PRIVATE).edit();
    
    editor.putBoolean(Scruin.FILTER_EN, filterEnBox.isChecked());
    editor.putBoolean(Scruin.RGB_EN, rgbEnBox.isChecked());
    editor.putBoolean(Scruin.TOOLET_EN, tooletEnBox.isChecked());
    editor.putBoolean(Scruin.RELATIVELY_EN, relativelyBox.isChecked());
    
    editor.putInt(Scruin.RED, rBar.getProgress());
    editor.putInt(Scruin.GREEN, gBar.getProgress());
    editor.putInt(Scruin.BLUE, bBar.getProgress());
    editor.putInt(Scruin.ALPHA, aBar.getProgress());
    
    editor.commit();
  }
  
  private void disableColor()
  {
    rBar.setEnabled(false);
    gBar.setEnabled(false);
    bBar.setEnabled(false);
    
    rButton.setEnabled(false);
    gButton.setEnabled(false);
    bButton.setEnabled(false);
  }
  
  private void disableAll()
  {
    Scruin.getApp().tooletView.remove();
    aBar.setEnabled(false);
    aButton.setEnabled(false);
    tooletEnBox.setEnabled(false);
    relativelyBox.setEnabled(false);
    
    rgbEnBox.setEnabled(false);
    disableColor();
  }
}
