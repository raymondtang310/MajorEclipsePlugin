package org.rayzor.mutantview.views;


import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import mutator.Major;


/**
 * This class is the workbench view for this plugin.
 * It displays a menu of each mutant by its number and
 * displays whether the mutant was killed or not after running tests against it.
 * Double-clicking on a mutant will open up the source file that was mutated
 * and highlight the line on which the mutant occurs.
 * 
 * @author Raymond Tang
 * 
 * <p><p>
 * Auto generated comment by Eclipse:
 * <p><p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class MutantView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.rayzor.mutantview.views.MutantView";

	private TableViewer viewer;
	private Action displayMutant;
	private Action highlightMutantInSource;
	private Action highlightMutantInMutatedSource;
	private Action sortKilledFirst;
	private Action sortAliveAndCoveredFirst;
	private Action sortUncoveredFirst;
	private Action sortNumberAsc;
	private Action doubleClickAction;
	// Major object which contains information about mutants and tests for some java file
	private Major m = null;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	/**
	 * Provides the mutant IDs to be listed in the view.
	 * 
	 * @author Raymond Tang
	 *
	 */
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		/**
		 * This method provides an array of all mutant IDs to the view.
		 */
		public Object[] getElements(Object parent) {
			if(m == null) return new Object[0];
			int numMutants = m.getNumberOfMutants();
			Integer[] mutantNumbers = new Integer[numMutants];
			for(int i = 1; i <= mutantNumbers.length; i++) {
				mutantNumbers[i-1] = i;
			}
			return mutantNumbers;
		}
	}
	
	/**
	 * Assigns images to items (mutants) listed in the view.
	 * A green plus sign is displayed next to a mutant ID if it is killed.
	 * Blue circles are displayed next to a mutant ID if it is covered but alive.
	 * A red X is displayed next to a mutant ID if it is uncovered.
	 * 
	 * @author Raymond Tang
	 *
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			if(m == null) return PlatformUI.getWorkbench().getSharedImages().
									getImage(ISharedImages.IMG_OBJ_ELEMENT);
			int mutantNumber = ((Integer)obj).intValue();
			// Display green plus sign next to mutant number if it is killed
			if(m.isMutantKilled(mutantNumber)) return PlatformUI.getWorkbench().getSharedImages().
														getImage(ISharedImages.IMG_OBJ_ADD);
			// Display blue circles next to mutant number if it is covered but alive
			if(m.isMutantCovered(mutantNumber)) return PlatformUI.getWorkbench().getSharedImages().
														getImage(ISharedImages.IMG_OBJ_ELEMENT);
			// Display red X next to mutant number if it is uncovered
			return PlatformUI.getWorkbench().getSharedImages().
					getImage(ISharedImages.IMG_DEC_FIELD_ERROR);
		}
	}
	
	/**
	 * This comparator is used to sort mutants listed in the view
	 * by their number in ascending order. 
	 * 
	 * @author Raymond Tang
	 *
	 */
	class MutantComparator extends ViewerComparator {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if(!(e1 instanceof Integer && e2 instanceof Integer)) return super.compare(viewer, e1, e2);
			int e1Int = ((Integer)e1).intValue();
			int e2Int = ((Integer)e2).intValue();
			return e1Int - e2Int;
		}
	}
	
	/**
	 * This comparator is used to sort mutants listed in the view.
	 * This comparator causes killed mutants to show up first, then mutants
	 * that are covered but alive, and uncovered mutants last. 
	 * 
	 * @author Raymond Tang
	 *
	 */
	class KilledMutantComparator extends MutantComparator {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if(!(e1 instanceof Integer && e2 instanceof Integer) || m == null) return super.compare(viewer, e1, e2);
			int e1Int = ((Integer)e1).intValue();
			int e2Int = ((Integer)e2).intValue();
			boolean e1Covered = m.isMutantCovered(e1Int);
			boolean e2Covered = m.isMutantCovered(e2Int);
			boolean e1Killed = m.isMutantKilled(e1Int);
			boolean e2Killed = m.isMutantKilled(e2Int);
			// If mutant e1 is covered and alive and mutant e2 is killed, e2 shows up earlier in the view
			if(e1Covered && !e1Killed && e2Killed) return 1;
			// If mutant e1 is covered and alive and mutant e2 is uncovered, e1 shows up earlier in the view
			if(e1Covered && !e1Killed && !e2Covered) return -1;
			// If mutant e1 is uncovered and mutant e2 is killed, e2 shows up earlier in the view
			if(!e1Covered && e2Killed) return 1;
			// If mutant e2 is covered and alive and mutant e1 is killed, e1 shows up earlier in the view
			if(e2Covered && !e2Killed && e1Killed) return -1;
			// If mutant e2 is covered and alive and mutant e1 is uncovered, e2 shows up earlier in the view
			if(e2Covered && !e2Killed && !e1Covered) return 1;
			// If mutant e2 is uncovered and mutant e1 is killed, e1 shows up earlier in the view
			if(!e2Covered && e1Killed) return -1;
			return e1Int - e2Int;
		}
	}
	
	/**
	 * This comparator is used to sort mutants listed in the view. 
	 * This comparator causes mutants that are covered but alive to show up first, 
	 * then uncovered mutants, and killed mutants last. 
	 * 
	 * @author Raymond Tang
	 *
	 */
	class AliveAndCoveredMutantComparator extends MutantComparator {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if(!(e1 instanceof Integer && e2 instanceof Integer) || m == null) return super.compare(viewer, e1, e2);
			int e1Int = ((Integer)e1).intValue();
			int e2Int = ((Integer)e2).intValue();
			boolean e1Covered = m.isMutantCovered(e1Int);
			boolean e2Covered = m.isMutantCovered(e2Int);
			boolean e1Killed = m.isMutantKilled(e1Int);
			boolean e2Killed = m.isMutantKilled(e2Int);
			// If mutant e1 is covered and alive and mutant e2 is killed, e1 shows up earlier in the view
			if(e1Covered && !e1Killed && e2Killed) return -1;
			// If mutant e1 is covered and alive and mutant e2 is uncovered, e1 shows up earlier in the view
			if(e1Covered && !e1Killed && !e2Covered) return -1;
			// If mutant e1 is uncovered and mutant e2 is killed, e1 shows up earlier in the view
			if(!e1Covered && e2Killed) return -1;
			// If mutant e2 is covered and alive and mutant e1 is killed, e2 shows up earlier in the view
			if(e2Covered && !e2Killed && e1Killed) return 1;
			// If mutant e2 is covered and alive and mutant e1 is uncovered, e2 shows up earlier in the view
			if(e2Covered && !e2Killed && !e1Covered) return 1;
			// If mutant e2 is uncovered and mutant e1 is killed, e2 shows up earlier in the view
			if(!e2Covered && e1Killed) return 1;
			return e1Int - e2Int;
		}
	}
	
	/**
	 * This comparator is used to sort mutants listed in the view. 
	 * This comparator causes uncovered mutants to show up first, then mutants 
	 * that are covered but alive, and killed mutants last. 
	 * 
	 * @author Raymond Tang
	 *
	 */
	class UncoveredMutantComparator extends MutantComparator {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if(!(e1 instanceof Integer && e2 instanceof Integer) || m == null) return super.compare(viewer, e1, e2);
			int e1Int = ((Integer)e1).intValue();
			int e2Int = ((Integer)e2).intValue();
			boolean e1Covered = m.isMutantCovered(e1Int);
			boolean e2Covered = m.isMutantCovered(e2Int);
			boolean e1Killed = m.isMutantKilled(e1Int);
			boolean e2Killed = m.isMutantKilled(e2Int);
			// If mutant e1 is covered and alive and mutant e2 is killed, e1 shows up earlier in the view
			if(e1Covered && !e1Killed && e2Killed) return -1;
			// If mutant e1 is covered and alive and mutant e2 is uncovered, e2 shows up earlier in the view
			if(e1Covered && !e1Killed && !e2Covered) return 1;
			// If mutant e1 is uncovered and mutant e2 is killed, e1 shows up earlier in the view
			if(!e1Covered && e2Killed) return -1;
			// If mutant e2 is covered and alive and mutant e1 is killed, e2 shows up earlier in the view
			if(e2Covered && !e2Killed && e1Killed) return 1;
			// If mutant e2 is covered and alive and mutant e1 is uncovered, e1 shows up earlier in the view
			if(e2Covered && !e2Killed && !e1Covered) return -1;
			// If mutant e2 is uncovered and mutant e1 is killed, e2 shows up earlier in the view
			if(!e2Covered && e1Killed) return 1;
			return e1Int - e2Int;
		}
	}

	/**
	 * The constructor.
	 */
	public MutantView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setComparator(new MutantComparator());
		viewer.setInput(getViewSite());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "org.rayzor.mutantView.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	/**
	 * Configures the context menu.
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MutantView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	/**
	 * Configures the pull down menu and toolbar.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Adds items to the pull down menu.
	 * 
	 * @param manager the pull down menu to add items to
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(sortKilledFirst);
		manager.add(sortAliveAndCoveredFirst);
		manager.add(sortUncoveredFirst);
		manager.add(sortNumberAsc);
	}

	/**
	 * Adds items to the context menu.
	 * 
	 * @param manager the menu to add items to
	 */
	private void fillContextMenu(IMenuManager manager) {
		manager.add(displayMutant);
		manager.add(highlightMutantInSource);
		manager.add(highlightMutantInMutatedSource);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	/**
	 * Adds items to the toolbar.
	 * 
	 * @param manager the toolbar to add items to
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(sortKilledFirst);
		manager.add(sortAliveAndCoveredFirst);
		manager.add(sortUncoveredFirst);
		manager.add(sortNumberAsc);
	}

	/**
	 * Provides functionality to buttons and clicking on items. 
	 */
	private void makeActions() {
		// Click on "Display Mutant" in context menu to show the selected mutant in a message box
		displayMutant = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				int mutantNumber = ((Integer)obj).intValue();
				try {
					ArrayList<String> mutantsLog = m.getMutantsLog();
					String logLine = mutantsLog.get(mutantNumber - 1);
					showMessage(logLine);
					
				} catch (FileNotFoundException e) {
					showMessage("mutants.log not found");
				}
			}
		};
		displayMutant.setText("Display Mutant");
		
		// Click on "Highlight Mutant in Source File" in context menu to highlight the line
		// in the source file in which the selected mutant occurs
		highlightMutantInSource = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				int mutantNumber = ((Integer)obj).intValue();
				m.highlightMutantInSource(mutantNumber);
			}
		};
		highlightMutantInSource.setText("Highlight Mutant in Source File");
		
		// Click on "Highlight Mutant in Mutated Source File" in context menu to highlight
		// the line in the mutated source file in which the selected mutant occurs
		highlightMutantInMutatedSource = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				int mutantNumber = ((Integer)obj).intValue();
				m.highlightMutantInMutatedSource(mutantNumber);
			}
		};
		highlightMutantInMutatedSource.setText("Highlight Mutant in Mutated Source File");
		
		// Sorts mutants listed in the view using the KilledMutantComparator
		sortKilledFirst = new Action() {
			public void run() {
				viewer.setComparator(new KilledMutantComparator());
			}
		};
		sortKilledFirst.setText("Show Killed Mutants First");
		sortKilledFirst.setToolTipText("Show Killed Mutants First");
		sortKilledFirst.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
		
		// Sorts mutants listed in the view using the AliveAndCoveredMutantComparator
		sortAliveAndCoveredFirst = new Action() {
			public void run() {
				viewer.setComparator(new AliveAndCoveredMutantComparator());
			}
		};
		sortAliveAndCoveredFirst.setText("Show Alive and Covered Mutants First");
		sortAliveAndCoveredFirst.setToolTipText("Show Alive and Covered Mutants First");
		sortAliveAndCoveredFirst.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));
		
		// Sorts mutants listed in the view using the UncoveredMutantComparator
		sortUncoveredFirst = new Action() {
			public void run() {
				viewer.setComparator(new UncoveredMutantComparator());
			}
		};
		sortUncoveredFirst.setText("Show Uncovered Mutants First");
		sortUncoveredFirst.setToolTipText("Show Uncovered Mutants First");
		sortUncoveredFirst.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR)); 
		
		// Sorts mutants listed in the view using the MutantComparator
		sortNumberAsc = new Action() {
			public void run() {
				viewer.setComparator(new MutantComparator());
			}
		};
		sortNumberAsc.setText("Sort by Number");
		sortNumberAsc.setToolTipText("Sort by Number");
		sortNumberAsc.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
		
		// Double click on a mutant to highlight the line in the 
		// source file in which the mutant occurs
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				int mutantNumber = ((Integer)obj).intValue();
				m.highlightMutantInSource(mutantNumber);
			}
		};
	}

	/**
	 * Makes it so doubleClickAction runs upon double clicking on a mutant in the view.
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	
	/**
	 * Opens up a message box containing the given message.
	 * 
	 * @param message the message to display
	 */
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Mutant View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	/**
	 * Provides Major object to this view. 
	 * 
	 * Throws an IllegalArgumentException if the given Major object is null.
	 * 
	 * @param m Major object
	 */
	public void setMajorObject(Major m) {
		if(m == null) throw new IllegalArgumentException("Major object cannot be null");
		this.m = m;
		viewer.setContentProvider(new ViewContentProvider());
	}
	
	
}