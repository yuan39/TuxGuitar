package org.herac.tuxguitar.cocoa.toolbar;

import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.cocoa.NSButton;
import org.eclipse.swt.internal.cocoa.NSString;
import org.eclipse.swt.internal.cocoa.NSToolbar;
import org.eclipse.swt.internal.cocoa.NSWindow;
import org.eclipse.swt.internal.cocoa.OS;
import org.eclipse.swt.widgets.Shell;
import org.herac.tuxguitar.cocoa.TGCocoa;

public class MacToolbar {
	
	private static final byte[] SWT_OBJECT = {'S', 'W', 'T', '_', 'O', 'B', 'J', 'E', 'C', 'T', '\0'};
	
    private static final int NSWindowToolbarButton = 3;
    
    private static final long sel_toolbarButtonClicked_ = TGCocoa.sel_registerName("toolbarButtonClicked:");
    
    private boolean enabled;
    
	public MacToolbar(){
		super();
	}
	
	public void init( Shell shell ) throws Throwable{
		Callback callback = new Callback( this , "callbackProc", 3 );
		long callbackProc = callback.getAddress();
		
		if( callbackProc != 0 ){
			String classname = ("MacToolbarDelegate");
			if( TGCocoa.objc_lookUpClass ( classname ) == 0 ) {
				long cls = TGCocoa.objc_allocateClassPair(OS.class_NSApplication, classname , 0 ) ;
				TGCocoa.class_addIvar(cls, SWT_OBJECT, C.PTR_SIZEOF , (byte)(C.PTR_SIZEOF == 4 ? 2 : 3), new byte[]{'*','\0'} );
				TGCocoa.class_addMethod(cls, sel_toolbarButtonClicked_, callbackProc , "@:@");
				TGCocoa.objc_registerClassPair(cls);
			}
			
			MacToolbarDelegate delegate = new MacToolbarDelegate();
			delegate.alloc().init();
			//TODO: The pointer returned by NewGlobalRef is not auto deleted.
			// We must to free the pointer when plugin is closed using "OS.DeleteGlobalRef".
			OS.object_setInstanceVariable( delegate.id , SWT_OBJECT , OS.NewGlobalRef( this ) );
			
			NSToolbar dummyBar = new NSToolbar();
			dummyBar.alloc();
			dummyBar.initWithIdentifier(NSString.stringWith("SWTToolbar")); //$NON-NLS-1$
			dummyBar.setVisible(false);
			
			NSWindow nsWindow = shell.view.window();
			nsWindow.setToolbar(dummyBar);
			dummyBar.release();
			nsWindow.setShowsToolbarButton(true);
			
			// Override the target and action of the toolbar button so we can
			// control it.
			NSButton toolbarButton = nsWindow.standardWindowButton(NSWindowToolbarButton);
			if (toolbarButton != null) {
				toolbarButton.setTarget( delegate );
				TGCocoa.setControlAction( toolbarButton , sel_toolbarButtonClicked_ );
			}
		}
	}
	
    public int callbackProc( long id, long sel, long arg0 ) {
    	if ( this.isEnabled() ){
	    	if ( sel == sel_toolbarButtonClicked_ ) {
	    		return handleToogleToolbarCommand();
	        }
    	}
        return OS.noErr;
    }
    
    public int callbackProc( int id, int sel, int arg0 ) {
    	return this.callbackProc( (long)id, (long)sel, (long)arg0);
    }
    
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int handleToogleToolbarCommand(){
		MacToolbarAction.toogleToolbar();
		return OS.noErr;
	}
}