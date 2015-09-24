#include "declarations.h"
#include "widgets/gnomedirentry.h"

void set_properties(GtkWidget *widget, GdsFile *temp);

void edit_properties(GtkWidget *widget, gpointer data)
{
   GtkWidget *props_window;
   GtkWidget *main_box;
   GtkWidget *settings_box;
   GtkWidget *wide_box;
   GtkWidget *general_frame;
   GtkWidget *general_box;

   GtkWidget *syntax_frame;
   GtkWidget *syntax_box;
   GtkWidget *bracket_frame;
   GtkWidget *bracket_box;
   GtkWidget *tabs_frame;
   GtkWidget *tabs_box;
   GtkWidget *compile_frame;
   GtkWidget *compile_box;
  
   GtkWidget *util_box;
   GtkWidget *util_label;

   GtkWidget *buttons_box;
   GtkWidget *ok_button;
   GtkWidget *defaults_button;
   GtkWidget *undo_button;
   GtkWidget *close_button;
   FileProps *file_props;
   gchar *chars = NULL;
   GtkWidget *hsep;

   if(cur_file->props_dialog && GTK_IS_WINDOW(cur_file->props_dialog))
   {
      gdk_window_raise(cur_file->props_dialog->window);
      return;
   }

   file_props = g_new(FileProps, 1);
   file_props->defaults = cur_file->tables ? &cur_file->tables->props : NULL;
   file_props->current = &cur_file->props;
   file_props->backups = g_new(GdsFileProperties, 1);

   copy_properties(file_props->backups, file_props->current);

   props_window = gtk_window_new(GTK_WINDOW_DIALOG);
   gtk_container_set_border_width(GTK_CONTAINER(props_window), 5);
   chars = g_strconcat(_("GdsFileProperties Editor - "), cur_file->filename, NULL);
   gtk_window_set_title(GTK_WINDOW(props_window), chars);
   g_free(chars);
   cur_file->props_dialog = props_window;

   main_box = gtk_vbox_new(FALSE, 0);
   gtk_container_add(GTK_CONTAINER(props_window), main_box);
   gtk_widget_show(main_box);

   settings_box = gtk_vbox_new(FALSE, 0);
   gtk_container_set_border_width(GTK_CONTAINER(settings_box), 0);
   gtk_box_pack_start(GTK_BOX(main_box), settings_box, FALSE, FALSE, 0);
   gtk_widget_show(settings_box);

   /* File Settings */
   general_frame = gtk_frame_new(_("File Specific Settings"));
   gtk_box_pack_start(GTK_BOX(settings_box), general_frame, FALSE, FALSE, 0);
   gtk_widget_show(general_frame);

   wide_box = gtk_hbox_new(FALSE, 5);
   gtk_container_set_border_width(GTK_CONTAINER(wide_box), 5);
   gtk_container_add(GTK_CONTAINER(general_frame), wide_box);
   gtk_widget_show(wide_box);

   general_box = gtk_vbox_new(FALSE, 0);
   gtk_box_pack_start(GTK_BOX(wide_box), general_box, TRUE, TRUE, 0);   
   gtk_widget_show(general_box);

   util_box = gtk_hbox_new(FALSE, 0);
   gtk_box_pack_start(GTK_BOX(general_box), util_box, TRUE, TRUE, 5);
   gtk_container_set_border_width(GTK_CONTAINER(util_box), 5);
   gtk_widget_show(util_box);
   file_props->over_ride = make_button(_("Override defaults ?"), util_box, &file_props->current->over_ride);

   util_box = gtk_hbox_new(FALSE, 5);
   gtk_box_pack_start(GTK_BOX(general_box), util_box, TRUE, TRUE, 5);
   gtk_widget_show(util_box);
   util_label = gtk_label_new(_("Directory:"));
   gtk_box_pack_start(GTK_BOX(util_box), util_label, FALSE, FALSE, 0);   
   gtk_widget_show(util_label);   
   file_props->dir = gnome_direntry_new();
   gtk_box_pack_start(GTK_BOX(util_box), file_props->dir, TRUE, TRUE, 0);   
   gtk_widget_show(file_props->dir);   

   bracket_frame = gtk_frame_new(_("Bracket Highlighting"));
   gtk_box_pack_start(GTK_BOX(general_box), bracket_frame, FALSE, FALSE, 0);
   gtk_widget_show(bracket_frame);
   bracket_box = gtk_vbox_new(FALSE, 0);
   gtk_container_set_border_width(GTK_CONTAINER(bracket_box), 5);
   gtk_container_add(GTK_CONTAINER(bracket_frame), bracket_box);   
   gtk_widget_show(bracket_box);
   util_box  = gtk_hbox_new(FALSE, 0);
   gtk_box_pack_start(GTK_BOX(bracket_box), util_box, FALSE, TRUE, 0);         
   gtk_widget_show(util_box);
   file_props->bracketmatch = make_button(_("Enable bracket highlighting ?"), util_box, &file_props->current->bracketmatch);

   syntax_frame = gtk_frame_new(_("Syntax Highlighting"));
   gtk_box_pack_start(GTK_BOX(general_box), syntax_frame, FALSE, FALSE, 0);
   gtk_widget_show(syntax_frame);
   syntax_box = gtk_vbox_new(FALSE, 0);
   gtk_container_set_border_width(GTK_CONTAINER(syntax_box), 5);
   gtk_container_add(GTK_CONTAINER(syntax_frame), syntax_box);   
   gtk_widget_show(syntax_box);
   util_box  = gtk_hbox_new(FALSE, 0);
   gtk_box_pack_start(GTK_BOX(syntax_box), util_box, FALSE, TRUE, 0);         
   gtk_widget_show(util_box);
   file_props->syntax = make_button(_("Enable syntax highlighting ?"), util_box, &file_props->current->syntax);

   tabs_frame = gtk_frame_new(_("Indentation"));
   gtk_box_pack_start(GTK_BOX(general_box), tabs_frame, TRUE, TRUE, 0);
   gtk_widget_show(tabs_frame);
   tabs_box = gtk_vbox_new(FALSE, 0);
   gtk_container_set_border_width(GTK_CONTAINER(tabs_box), 5);
   gtk_container_add(GTK_CONTAINER(tabs_frame), tabs_box);   
   gtk_widget_show(tabs_box);

   util_box = gtk_hbox_new(FALSE, 0);
   gtk_container_set_border_width(GTK_CONTAINER(util_box), 5);
   gtk_box_pack_start(GTK_BOX(tabs_box), util_box, TRUE, TRUE, 0);
   gtk_widget_show(util_box);
   file_props->auto_indent = make_button(_("Auto Indent ?"), util_box, &file_props->current->auto_indent);

   util_box = gtk_hbox_new(FALSE, 0);
   gtk_container_set_border_width(GTK_CONTAINER(util_box), 5);
   gtk_box_pack_start(GTK_BOX(tabs_box), util_box, TRUE, TRUE, 0);
   gtk_widget_show(util_box);
   file_props->use_spaces = make_button(_("Use spaces instead of tabs ?"), util_box, &file_props->current->use_spaces);

   util_box = gtk_hbox_new(FALSE, 0);
   gtk_container_set_border_width(GTK_CONTAINER(util_box), 5);
   gtk_box_pack_start(GTK_BOX(tabs_box), util_box, TRUE, TRUE, 0);
   gtk_widget_show(util_box);
   util_label = gtk_label_new(_("Tab stop:"));
   gtk_box_pack_start(GTK_BOX(util_box), util_label, FALSE, FALSE, 0);
   gtk_widget_show(util_label);
   file_props->spaces_spin = gtk_spin_button_new(GTK_ADJUSTMENT(gtk_adjustment_new(0, 1, 8, 1, 2, 0)), 1, 0);
   gtk_box_pack_start(GTK_BOX(util_box), file_props->spaces_spin, TRUE, TRUE, 0);
   gtk_widget_show(file_props->spaces_spin);

   compile_frame = gtk_frame_new(_("Build Information"));
   gtk_box_pack_start(GTK_BOX(wide_box), compile_frame, TRUE, TRUE, 0);
   gtk_widget_show(compile_frame);
   compile_box = gtk_vbox_new(FALSE, 0);
   gtk_container_set_border_width(GTK_CONTAINER(compile_box), 5);
   gtk_container_add(GTK_CONTAINER(compile_frame), compile_box);   
   gtk_widget_show(compile_box);

   util_box = gtk_hbox_new(FALSE, 0);
   gtk_box_pack_start(GTK_BOX(compile_box), util_box, TRUE, TRUE, 5);
   gtk_widget_show(util_box);
   util_label = gtk_label_new(_("Compiler: "));
   gtk_box_pack_start(GTK_BOX(util_box), util_label, FALSE, FALSE, 0);   
   gtk_widget_show(util_label);
   file_props->compiler = gtk_entry_new();
   gtk_box_pack_start(GTK_BOX(util_box), file_props->compiler, TRUE, TRUE, 0);
   gtk_widget_show(file_props->compiler);

   util_box = gtk_hbox_new(FALSE, 0);
   gtk_box_pack_start(GTK_BOX(compile_box), util_box, TRUE, TRUE, 5);
   gtk_widget_show(util_box);
   util_label = gtk_label_new(_("Debugger: "));
   gtk_box_pack_start(GTK_BOX(util_box), util_label, FALSE, FALSE, 0);   
   gtk_widget_show(util_label);
   file_props->debugger = gtk_entry_new();
   gtk_box_pack_start(GTK_BOX(util_box), file_props->debugger, TRUE, TRUE, 0);
   gtk_widget_show(file_props->debugger);

   util_box = gtk_hbox_new(FALSE, 0);
   gtk_box_pack_start(GTK_BOX(compile_box), util_box, TRUE, TRUE, 5);
   gtk_widget_show(util_box);
   util_label = gtk_label_new(_("Execution: "));
   gtk_box_pack_start(GTK_BOX(util_box), util_label, FALSE, FALSE, 0);   
   gtk_widget_show(util_label);
   file_props->execution = gtk_entry_new();
   gtk_box_pack_start(GTK_BOX(util_box), file_props->execution, TRUE, TRUE, 0);
   gtk_widget_show(file_props->execution);
   
   hsep = gtk_hseparator_new();
   gtk_box_pack_start(GTK_BOX(main_box), hsep, FALSE, TRUE, 10);
   gtk_widget_show(hsep);

   buttons_box = gtk_hbutton_box_new();
   gtk_box_pack_start(GTK_BOX(main_box), buttons_box, FALSE, TRUE, 0);
   gtk_button_box_set_layout(GTK_BUTTON_BOX(buttons_box), gnome_preferences_get_button_layout());
   gtk_button_box_set_spacing(GTK_BUTTON_BOX(buttons_box), GNOME_PAD);
   gtk_widget_show(buttons_box);

   ok_button = gnome_stock_button(GNOME_STOCK_BUTTON_OK);
   gtk_box_pack_start(GTK_BOX(buttons_box), ok_button, FALSE, TRUE, 0);
   gtk_signal_connect(GTK_OBJECT(ok_button), "clicked", GTK_SIGNAL_FUNC(save_properties), file_props);
   gtk_signal_connect(GTK_OBJECT(ok_button), "clicked", GTK_SIGNAL_FUNC(set_properties), cur_file);
   gtk_signal_connect(GTK_OBJECT(ok_button), "clicked", GTK_SIGNAL_FUNC(kill_widget), GTK_OBJECT(props_window));
   GTK_WIDGET_SET_FLAGS(ok_button, GTK_CAN_DEFAULT);
   gtk_widget_show(ok_button);

   defaults_button = gnome_stock_or_ordinary_button(_("Defaults"));
   gtk_box_pack_start(GTK_BOX(buttons_box), defaults_button, FALSE, TRUE, 0);
   gtk_signal_connect(GTK_OBJECT(defaults_button), "clicked", GTK_SIGNAL_FUNC(get_default_props), file_props);
   GTK_WIDGET_SET_FLAGS(defaults_button, GTK_CAN_DEFAULT);
   gtk_widget_show(defaults_button);

   undo_button = gnome_stock_or_ordinary_button(_("Undo"));
   gtk_box_pack_start(GTK_BOX(buttons_box), undo_button, FALSE, TRUE, 0);   
   gtk_signal_connect(GTK_OBJECT(undo_button), "clicked", GTK_SIGNAL_FUNC(get_backup_props), file_props);
   GTK_WIDGET_SET_FLAGS(undo_button, GTK_CAN_DEFAULT);
   gtk_widget_show(undo_button);

   close_button = gnome_stock_button(GNOME_STOCK_BUTTON_CLOSE);
   gtk_box_pack_end(GTK_BOX(buttons_box), close_button, FALSE, TRUE, 0);   
   gtk_signal_connect(GTK_OBJECT(close_button), "clicked", GTK_SIGNAL_FUNC(kill_widget), GTK_OBJECT(props_window));
   gtk_signal_connect(GTK_OBJECT(close_button), "clicked", GTK_SIGNAL_FUNC(redo_backup_props), file_props);
   GTK_WIDGET_SET_FLAGS(close_button, GTK_CAN_DEFAULT);
   gtk_widget_grab_default(close_button);
   gtk_widget_show(close_button);

   gtk_signal_connect(GTK_OBJECT(props_window), "destroy", GTK_SIGNAL_FUNC(kill_widget_ptr), &cur_file->props_dialog);
   gtk_signal_connect(GTK_OBJECT(props_window), "destroy", GTK_SIGNAL_FUNC(free_properties), file_props->backups);
   gtk_signal_connect(GTK_OBJECT(props_window), "destroy", GTK_SIGNAL_FUNC(kill_data), file_props);
   put_properties(file_props, file_props->current);
   gtk_widget_show(props_window);
}


void save_properties(GtkWidget *widget, FileProps *props)
{
   get_properties(props, props->current);
}

void set_properties(GtkWidget *widget, GdsFile *temp)
{
   gboolean highlight;
   highlight = GDS_EDITOR(temp->text)->highlight;
   if(temp->props.over_ride)
   {
      if(highlight != temp->props.syntax)
         gds_editor_set_highlight(GDS_EDITOR(temp->text), temp->props.syntax);
      GTK_EXTEXT(temp->text)->default_tab_width = temp->props.spaces;
      gtk_widget_queue_draw(temp->text);
   }
   else
   {
      if(highlight != general_preferences.syntax)
         gds_editor_set_highlight(GDS_EDITOR(temp->text), general_preferences.syntax);
      GTK_EXTEXT(temp->text)->default_tab_width = general_preferences.spaces;
      gtk_widget_queue_draw(temp->text);
   }
}

void get_backup_props(GtkWidget *widget, FileProps *props)
{
   put_properties(props, props->backups);
}

void redo_backup_props(GtkWidget *widget, FileProps *props)
{
   copy_properties(props->current, props->backups);
}

void get_default_props(GtkWidget *widget, FileProps *props)
{
   if(props->defaults)
      put_properties(props, props->defaults);
}

void get_properties(FileProps *props, GdsFileProperties *source)
{
   g_free(source->dir);
   source->dir = gnome_direntry_get_dir(GNOME_DIRENTRY(props->dir));
   source->spaces = gtk_spin_button_get_value_as_int(GTK_SPIN_BUTTON(props->spaces_spin));
   g_free(source->compiler);
   source->compiler = g_strdup(gtk_entry_get_text(GTK_ENTRY(props->compiler)));
   g_free(source->debugger);
   source->debugger = g_strdup(gtk_entry_get_text(GTK_ENTRY(props->debugger)));
   g_free(source->execution);
   source->execution = g_strdup(gtk_entry_get_text(GTK_ENTRY(props->execution)));   
}

void put_properties(FileProps *props, GdsFileProperties *source)
{
   gtk_toggle_button_set_state(GTK_TOGGLE_BUTTON(props->over_ride), source->over_ride);
   if(source->dir) gnome_direntry_set_dir(GNOME_DIRENTRY(props->dir), source->dir);
   gtk_toggle_button_set_state(GTK_TOGGLE_BUTTON(props->bracketmatch), source->bracketmatch);
   gtk_toggle_button_set_state(GTK_TOGGLE_BUTTON(props->syntax), source->syntax);
   gtk_toggle_button_set_state(GTK_TOGGLE_BUTTON(props->auto_indent), source->auto_indent);
   gtk_toggle_button_set_state(GTK_TOGGLE_BUTTON(props->use_spaces), source->use_spaces);
   gtk_spin_button_set_value(GTK_SPIN_BUTTON(props->spaces_spin), (gfloat)source->spaces);
   if(source->compiler)
      gtk_entry_set_text(GTK_ENTRY(props->compiler), source->compiler);
   else
      gtk_entry_set_text(GTK_ENTRY(props->compiler), "");
   if(source->debugger)
      gtk_entry_set_text(GTK_ENTRY(props->debugger), source->debugger);
   else
      gtk_entry_set_text(GTK_ENTRY(props->debugger), "");
   if(source->execution)
      gtk_entry_set_text(GTK_ENTRY(props->execution), source->execution);
   else
      gtk_entry_set_text(GTK_ENTRY(props->execution), "");
}

void copy_properties(GdsFileProperties *dest, GdsFileProperties *source)
{
   memcpy(dest, source, sizeof(GdsFileProperties));
   if(source->dir)
      dest->dir = g_strdup(source->dir);
   if(source->compiler)
      dest->compiler = g_strdup(source->compiler);
   if(source->debugger)
      dest->debugger = g_strdup(source->debugger);
   if(source->execution)
      dest->execution = g_strdup(source->execution);
}

void kill_data(GtkWidget *widget, gpointer data)
{
   g_free(data);
}

void free_properties(GtkWidget *widget, GdsFileProperties *props)
{
   g_free(props->dir);
   g_free(props->compiler);
   g_free(props->debugger);
   g_free(props->execution);
   g_free(props);
}
