package ui;

import java.io.FileNotFoundException;
import java.util.List;

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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import analyzer.Mutant;
import analyzer.MutantAnalyzer;
import mutator.Mutator;


/**
 * This class is the workbench view for this plugin.
 * It displays a menu of each mutant by its ID and
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
	private MutantProvider mutantIDProvider;
	private ImageLabelProvider imageLabelProvider;
	private Action displayMutant;
	private Action highlightMutantInSource;
	private Action highlightMutantInMutatedSource;
	private Action sortKilledFirst;
	private Action sortAliveAndCoveredFirst;
	private Action sortUncoveredFirst;
	private Action sortIDAsc;
	private Action doubleClickAction;
	// Mutator which contains information about mutants for some java file
	private Mutator mutator = null;
	// MutantAnalyzer which contains information about mutants and tests for some java file
	private MutantAnalyzer analyzer = null;

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
		mutantIDProvider = new MutantProvider();
		imageLabelProvider = new ImageLabelProvider();
		viewer.setContentProvider(mutantIDProvider);
		viewer.setLabelProvider(imageLabelProvider);
		viewer.setComparator(new MutantIDComparator());
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
		manager.add(sortIDAsc);
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
		manager.add(sortIDAsc);
	}

	/**
	 * Provides functionality to buttons and clicking on items. 
	 */
	private void makeActions() {
		createDisplayMutantAction();
		createHighlightMutantInSourceAction();
		createHighlightMutantInMutatedSourceAction();
		createSortKilledFirstAction();
		createSortAliveAndCoveredFirstAction();
		createSortUncoveredFirstAction();
		createSortIDAscAction();
		createDoubleClickAction();
	}
	
	private void createDisplayMutantAction() {
		// Click on "Display Mutant" in context menu to show the selected mutant in a message box
		displayMutant = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				int mutantID = ((Mutant)obj).getID();
				try {
					List<String> mutantsLog = mutator.getMutantsLog();
					String logLine = mutantsLog.get(mutantID - 1);
					showMessage(logLine);
					
				} catch (FileNotFoundException e) {
					showMessage("mutants.log not found");
				}
			}
		};
		displayMutant.setText("Display Mutant");		
	}
	
	private void createHighlightMutantInSourceAction() {
		// Click on "Highlight Mutant in Source File" in context menu to highlight the line
		// in the source file in which the selected mutant occurs
		highlightMutantInSource = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				int mutantID = ((Mutant)obj).getID();
				MutantHighlighter highlighter = new MutantHighlighter(mutator);
				highlighter.highlightMutantInSource(mutantID);
			}
		};
		highlightMutantInSource.setText("Highlight Mutant in Source File");		
	}
	
	private void createHighlightMutantInMutatedSourceAction() {
		// Click on "Highlight Mutant in Mutated Source File" in context menu to highlight
		// the line in the mutated source file in which the selected mutant occurs
		highlightMutantInMutatedSource = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				int mutantID = ((Mutant)obj).getID();
				MutantHighlighter highlighter = new MutantHighlighter(mutator);
				highlighter.highlightMutantInMutatedSource(mutantID);
			}
		};
		highlightMutantInMutatedSource.setText("Highlight Mutant in Mutated Source File");		
	}
	
	private void createSortKilledFirstAction() {
		// Sorts mutants listed in the view using the KilledMutantComparator
		sortKilledFirst = new Action() {
			public void run() {
				KilledMutantComparator comparator = new KilledMutantComparator();
				comparator.setKillMatrix(analyzer);
				viewer.setComparator(comparator);
			}
		};
		sortKilledFirst.setText("Show Killed Mutants First");
		sortKilledFirst.setToolTipText("Show Killed Mutants First");
		sortKilledFirst.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
	}
	
	private void createSortAliveAndCoveredFirstAction() {
		// Sorts mutants listed in the view using the AliveAndCoveredMutantComparator
		sortAliveAndCoveredFirst = new Action() {
			public void run() {
				AliveAndCoveredMutantComparator comparator = new AliveAndCoveredMutantComparator();
				comparator.setKillMatrix(analyzer);
				viewer.setComparator(comparator);
			}
		};
		sortAliveAndCoveredFirst.setText("Show Alive and Covered Mutants First");
		sortAliveAndCoveredFirst.setToolTipText("Show Alive and Covered Mutants First");
		sortAliveAndCoveredFirst.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));
	}
	
	private void createSortUncoveredFirstAction() {
		// Sorts mutants listed in the view using the UncoveredMutantComparator
		sortUncoveredFirst = new Action() {
			public void run() {
				UncoveredMutantComparator comparator = new UncoveredMutantComparator();
				comparator.setKillMatrix(analyzer);
				viewer.setComparator(comparator);
			}
		};
		sortUncoveredFirst.setText("Show Uncovered Mutants First");
		sortUncoveredFirst.setToolTipText("Show Uncovered Mutants First");
		sortUncoveredFirst.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR));
	}
	
	private void createSortIDAscAction() {
		// Sorts mutants listed in the view using the MutantComparator
		sortIDAsc = new Action() {
			public void run() {
				viewer.setComparator(new MutantIDComparator());
			}
		};
		sortIDAsc.setText("Sort by ID");
		sortIDAsc.setToolTipText("Sort by ID");
		sortIDAsc.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_DEF_VIEW));
	}
	
	private void createDoubleClickAction() {
		// Double click on a mutant to highlight the line in the 
		// source file in which the mutant occurs
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				int mutantID = ((Mutant)obj).getID();
				MutantHighlighter highlighter = new MutantHighlighter(mutator);
				highlighter.highlightMutantInSource(mutantID);
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
	 * Provides a MutantAnalyzer to this view. 
	 * 
	 * @param analyzer a MutantAnalyzer
	 */
	public void setMutantAnalyzer(MutantAnalyzer analyzer) {
		this.mutator = analyzer.getMutator();
		this.analyzer = analyzer;
		mutantIDProvider.setMutator(mutator);
		imageLabelProvider.setMutantAnalyzer(this.analyzer);
		viewer.setContentProvider(mutantIDProvider);
		viewer.setLabelProvider(imageLabelProvider);
	}
	
}
