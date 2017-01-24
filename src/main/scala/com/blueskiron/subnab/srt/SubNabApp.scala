package com.blueskiron.subnab.srt

import java.awt.Color
import java.awt.Cursor.WAIT_CURSOR
import java.awt.Cursor.getPredefinedCursor
import java.awt.Dimension
import java.io.File
import java.io.PrintWriter

import scala.annotation.tailrec
import scala.swing.Action
import scala.swing.Alignment
import scala.swing.BorderPanel
import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.Component
import scala.swing.Dialog
import scala.swing.Dialog.Message.Error
import scala.swing.FileChooser
import scala.swing.FileChooser.Result.Approve
import scala.swing.FlowPanel
import scala.swing.GridBagPanel
import scala.swing.GridBagPanel.Fill
import scala.swing.GridPanel
import scala.swing.Insets
import scala.swing.Label
import scala.swing.MainFrame
import scala.swing.Menu
import scala.swing.MenuBar
import scala.swing.MenuItem
import scala.swing.Orientation
import scala.swing.ScrollPane
import scala.swing.Separator
import scala.swing.Slider
import scala.swing.TextArea
import scala.swing.TextField
import scala.swing.event.ButtonClicked
import scala.swing.event.ValueChanged
import scala.util.Failure
import scala.util.Success
import scala.util.Try

import org.slf4j.LoggerFactory

import com.blueskiron.subnab.srt.Parser.InvalidSRTContentException

import de.sciss.swingplus.Spinner
import javax.swing.BorderFactory
import javax.swing.SpinnerNumberModel
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter
import javax.swing.text.Document
import rx.lang.scala.Subscriber
import rx.lang.scala.Subscription
import rx.lang.scala.subjects.PublishSubject

/**
 * @author Juraj Zachar
 *
 */
class SubNabApp extends MainFrame {
  sealed trait CaptionOperation
  case object Add extends CaptionOperation
  case object Replace extends CaptionOperation
  case object Delete extends CaptionOperation

  val controller = new Controller(this)

  //menu
  menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("New") {
        controller.onNewFile()
      })
      contents += new MenuItem(Action("Open") {
        controller.onLoadFile()
      })
      contents += new MenuItem(Action("Save") {
        controller.onSaveFile()
      })
      contents += new MenuItem(Action("Save As...") {
        controller.onSaveAsFile()
      })
      contents += new MenuItem(Action("Close") {
        controller.onClose(false)
      })
      contents += new Separator
      contents += new MenuItem(Action("Quit") {
        controller.onClose(true)
      })
    }
    contents += new Menu("Edit") {
      contents += new MenuItem(Action("Shift Time") {
        println("Action '" + title + "' invoked")
      })
      contents += new MenuItem(Action("Set Encoding") {
        println("Action '" + title + "' invoked")
      })
    }
    contents += new Menu("Search") {
      contents += searchPanel
    }
  }

  lazy val newFileBtn = new Button("New")
  lazy val loadFileBtn = new Button("Load")
  lazy val saveFileBtn = new Button("Save")
  lazy val captionAddBtn = new Button("Add")
  lazy val captionReplaceBtn = new Button("Replace")
  lazy val captionDeleteBtn = new Button("Delete")
  lazy val searchField = new TextField(20) {
    //TODO
    text = "not implemented"
  }
  lazy val nextMatchBtn = new Button("Next")
  lazy val searchPanel = new FlowPanel {
    //only show if there are matches found
    nextMatchBtn.visible = false
    searchLabel.visible = false
    contents += searchField
    contents += searchLabel
    contents += nextMatchBtn
  }
  lazy val seqIdField = new TextField(5) {
    horizontalAlignment = Alignment.Right
  }
  lazy val beginField = new TextField(10) {
    horizontalAlignment = Alignment.Right
  }
  lazy val endField = new TextField(10) {
    horizontalAlignment = Alignment.Right
  }
  lazy val captionTextArea = new TextField(50)
  lazy val captionAllArea = new TextArea() {
    background = Color.DARK_GRAY
    foreground = Color.WHITE
    border = BorderFactory.createCompoundBorder(
      BorderFactory.createBevelBorder(1),
      BorderFactory.createEmptyBorder(2, 2, 2, 2)
    )
  }
  val highlighter = captionAllArea.peer.getHighlighter
  val painter = new DefaultHighlightPainter(Color.BLACK)
  lazy val sequenceSpinnerLabel, timeDistanceSliderLabel, searchLabel = new Label() {
    horizontalAlignment = Alignment.Leading
  }
  sequenceSpinnerLabel.text = "Sequence: "
  timeDistanceSliderLabel.text = "Time: "
  searchLabel.text = "(Found: 0)"
  val sequenceSpinnerModel = new SpinnerNumberModel(1, 1, 1, 1)
  val sequenceSpinner = new Spinner(sequenceSpinnerModel)
  val timeDistanceSlider = new Slider() {
    min = 1
    max = 1
  }

  listenTo(
    searchField,
    nextMatchBtn,
    sequenceSpinner,
    timeDistanceSlider,
    captionAddBtn,
    captionReplaceBtn,
    captionDeleteBtn
  )

  reactions += {
    //forward all UI events to the bus
    case ButtonClicked(`captionAddBtn`) => controller.onCaptionModify(Add)
    case ButtonClicked(`captionReplaceBtn`) => controller.onCaptionModify(Replace)
    case ButtonClicked(`captionDeleteBtn`) => controller.onCaptionModify(Delete)
    case ButtonClicked(`nextMatchBtn`) => controller.onNextMatch()
    case ValueChanged(`searchField`) => controller.onSearchField()
    case ValueChanged(`sequenceSpinner`) => controller.onSequenceSpinner()
    case ValueChanged(`timeDistanceSlider`) => controller.onTimeDistanceSlider()
  }

  val captionPanel = new GridBagPanel {
    private val c = new Constraints {
      fill = Fill.Horizontal
      gridx = 0
      gridy = 0;
      weightx = 0.5
      insets = new Insets(5, 5, 5, 5)
    }

    add(new FlowPanel {
      contents += new Label("Sequence: ")
      contents += seqIdField
    })
    add(new FlowPanel {
      contents += new Label("Begin:")
      contents += beginField
      contents += new Label("End:")
      contents += endField
    })

    add(new FlowPanel {
      contents += new Label("Caption:")
      contents += captionTextArea
    })

    add(new FlowPanel {
      contents += captionAddBtn
      contents += captionReplaceBtn
      contents += captionDeleteBtn
    })

    //Add component below previous one
    private def add(component: Component) {
      c.gridy += 1
      layout(component) = c
    }

  }

  val sidebarControls = new GridBagPanel {
    private val c = new Constraints {
      fill = Fill.Horizontal
      gridx = 0
      gridy = 0;
      weightx = 0.5
      insets = new Insets(5, 5, 5, 5)
    }

    add(new BoxPanel(Orientation.Horizontal) {
      contents += sequenceSpinnerLabel
      contents += sequenceSpinner
    })

    add(new GridPanel(2, 1) {
      contents += timeDistanceSliderLabel
      contents += timeDistanceSlider
    })

    //Add component below previous one
    private def add(component: Component) {
      c.gridy += 1
      layout(component) = c
    }
    preferredSize = new Dimension(200, 800)
    maximumSize = preferredSize
  }

  val captionScrollPane = new ScrollPane(captionAllArea) {
    captionAllArea.editable = false
    preferredSize = new Dimension(800 - 200, 800)
  }

  contents = new BorderPanel() {
    add(sidebarControls, BorderPanel.Position.West)
    add(new BorderPanel() {
      add(captionPanel, BorderPanel.Position.South)
      add(captionScrollPane, BorderPanel.Position.Center)
      preferredSize = new Dimension(800, 800)
    }, BorderPanel.Position.East)
    controller.initState
  }

  def toggleSessionVisibility(visible: Boolean) = {
    for (
      component <- List(captionAllArea, captionPanel, sequenceSpinnerLabel, sequenceSpinner,
        timeDistanceSliderLabel, timeDistanceSlider)
    ) {
      component.visible = visible
    }
  }

  centerOnScreen()

  class Controller(main: MainFrame) {

    sealed trait AppEvent
    case class Current(entries: List[Entry], filePath: Option[String], syncedToFs: Boolean) extends AppEvent
    case class Save(path: Option[String]) extends AppEvent
    case class Close(withSysExit: Boolean) extends AppEvent
    case class Search(text: String) extends AppEvent
    case class Get(seqId: Int, fromTextPosition: Int) extends AppEvent
    case class Modify(func: (List[Entry]) => Try[List[Entry]]) extends AppEvent
    case class SearchMatch(seqId: Int, from: Int, position: Int, lenght: Int) extends AppEvent
    case class Found(matches: List[SearchMatch]) extends AppEvent
    case object NextSearchMatch extends AppEvent
    private val log = LoggerFactory.getLogger(this.getClass)
    private val bus = PublishSubject[AppEvent]
    private val syncedSymbol = "  *Unsaved"
    private lazy val fileChooser = new FileChooser(new File("src/test/resources"))
    fileChooser.peer.setFileFilter(new FileNameExtensionFilter("SRT Subtitles Files", "srt"))

    def initState = {
      main.title = s"SubNab - SRT Subtitles Editor"
      toggleSessionVisibility(false)
      stateHandler(Current(Nil, None, true))
    }

    //internal state mutates with incoming and outgoing subscribers
    def stateHandler(state: Current): Subscription = {
      bus.subscribe(new Subscriber[AppEvent] {
        //success channel
        override def onNext(event: AppEvent) = {
          event match {
            //unsubscribe and allow new subscriber to serve new state
            case otherState: Current => {
              unsubscribe //self
              stateHandler(otherState)
              goToCaptionPosition(1)
            }
            case Save(None) => {
              if (!state.filePath.isDefined) {
                onSaveAsFile()
              } else {
                bus.onNext(Save(Some(state.filePath.get)))
              }
            }
            case Save(Some(path)) => {
              val fullPath = {
                if (!path.endsWith(".srt")) path + ".srt"
                else path
              }
              writeToFile(state.entries, fullPath)
              loadSource(state.entries, Some(fullPath), true)
            }
            case Modify(func) => {
              func(state.entries) match {
                //re-issue new state, this will trigger unsubscribing of this
                case Success(modifiedState) => loadSource(modifiedState, state.filePath, false)
                case Failure(t) => handleError(t)
              }
            }
            case Get(seq, fromTextPosition) => {
              state.entries.find(e => e.seq == seq).map(entry => serveCaption(entry, fromTextPosition))
            }
            case Close(false) => {
              if (!state.syncedToFs) {
                askToSaveBeforeClose(state, this)
              } else initState
            }
            case Close(true) => {
              if (!state.syncedToFs) {
                askToSaveBeforeClose(state, this)
              }
              bus.onCompleted()
              unsubscribe
              main.closeOperation()
            }
            case msg => //ignore the rest
          }
        }
      })
    }

    private def askToSaveBeforeClose(state: Current, subscriber: Subscriber[_]) {
      val result = Dialog.showConfirmation(main, "Discard unsaved work?", "Close", Dialog.Options.YesNoCancel)
      result match {
        // yes, discard
        case Dialog.Result.Yes => {
          subscriber.unsubscribe()
          initState
        }
        //no save
        case Dialog.Result.No => bus.onNext(Save(state.filePath))
        case Dialog.Result.Cancel => //do nothing
      }
    }

    def searchMatchHandler(matches: List[SearchMatch]) {
      bus.subscribe(new Subscriber[AppEvent] {
        override def onNext(event: AppEvent) = {
          event match {
            //unsubscribe and thus prevent dupes if other entries are served
            case Found(matches) => unsubscribe
            //a simple circulation of matches --> move head to the end of the list
            case NextSearchMatch => {
              val next = matches.tail.head
              searchMatchHandler(matches.tail ++ List(matches.head)); unsubscribe
            }
            case msg => log.warn(s"Ignoring: $msg")
          }
        }
      })
    }

    //UI events
    def onNextMatch() {
      //TODO
    }

    def onNewFile() {
      main.title = "SubNab - New"
      val entry = Entry(1, Time(0, 0, 0, 0), Time(0, 0, 0, 1), "...")
      sequenceSpinnerModel.setMaximum(1)
      timeDistanceSlider.max = 1
      captionAllArea.text = ""
      setCaption(entry)
      toggleSessionVisibility(true)
      loadSource(Nil, None, true)
    }

    def onSaveFile() {
      bus.onNext(Save(None))
    }

    def onSaveAsFile() {
      if (fileChooser.showSaveDialog(main) == Approve) {
        val filePath = fileChooser.selectedFile.getAbsolutePath
        bus.onNext(Save(Some(filePath)))
      }
    }

    def onClose(withSysExit: Boolean) {
      bus.onNext(Close(withSysExit))
    }

    def onLoadFile() {
      waitCursor {
        // Ask for the file location
        if (fileChooser.showOpenDialog(main) != Approve) return
        // Load the file
        val path = fileChooser.selectedFile.getAbsolutePath
        if (path == null) return
        else {
          val fileContents = scala.io.Source.fromFile(path, "utf-8")
          if (fileContents == null) {
            val errorMsg = s"File: '$path' could not be opened!"
            handleError(new Exception(errorMsg))
          } else {
            Parser.parse(fileContents) match {
              case Failure(t) => handleError(t)
              case Success(entries) => loadSource(entries, Some(path), true)
            }
          }
        }
      }
    }

    private def loadSource(entries: List[Entry], filePath: Option[String], isSynced: Boolean) {
      //set title 
      main.title = s"SubNab - ${filePath.getOrElse("New")}"
      setSavedInTitle(isSynced)
      val maxSeqId = getMaxSeqId(entries)
      sequenceSpinnerModel.setMaximum(maxSeqId)
      timeDistanceSlider.max = maxSeqId
      captionAllArea.text = entries.map(_.toString).mkString
      toggleSessionVisibility(true)
      //emit to invalidate old state handlers
      val state = Current(entries, filePath, isSynced)
      bus.onNext(state)
    }

    def onSearchField() {
      log.debug(s"STUB: reacting to ${searchField.text}")
    }

    def onSequenceSpinner() { onSequence(sequenceSpinner.value.asInstanceOf[Int]) }

    def onTimeDistanceSlider() { onSequence(timeDistanceSlider.value.asInstanceOf[Int]) }

    def onCaptionModify(operation: CaptionOperation) {
      val trySeqId = Parser.validateSequenceId(seqIdField.text)
      val begin = beginField.text
      val end = endField.text
      val tryTime = Parser.validateTime(begin, end)
      val caption = captionTextArea.text
      val tryCaption = if (caption.isEmpty()) Failure(InvalidSRTContentException("Caption cannot be empty")) else Success(caption)
      (trySeqId, tryTime, tryCaption) match {
        case (Success(seqId), Success(time), Success(caption)) => {
          val mod = Modify {
            entries =>
              {
                val lastSeqId = getMaxSeqId(entries)
                operation match {
                  case Delete => {
                    if (!entries.map(_.seq).contains(seqId)) {
                      Failure(new Exception(s"Cannot delete '$seqId'. Caption '$seqId' not in the sequence."))
                    } else {
                      //Success(entries.filter(_.seq != seqId))
                      //this needs re-sequencing the captions
                      val modList = entries.filter(_.seq != seqId).sortBy(_.seq)
                        .foldLeft((1, List[Entry]())) { case (y, e) => (y._1 + 1, e.copy(seq = y._1) :: y._2) }
                      Success(modList._2.reverse)
                    }
                  }
                  case Add => {
                    //not dealing with 'next" id  or 'existing' id is a failure
                    val distance = seqId - lastSeqId
                    if (distance > 1 && !entries.map(_.seq).contains(seqId)) {
                      val missing = (for (i <- lastSeqId + 1 to seqId - 1) yield i).mkString(", ").take(10) + " ..."
                      Failure(new Exception(s"Cannot add '$seqId' to the source. Last sequence: '$lastSeqId', missing: '$missing'"))
                    } else {
                      //treat duplicate insertion index id by shifting the existing indices one up
                      //e.g. ([3],1,2,3,4,5) --> (1,2,[3],3,4,5) ---> (1,2,[3],4,5,6)
                      val modList = { Entry(seqId, time._1, time._2, caption) :: entries }.sortBy(_.seq)
                        .foldLeft((List[Entry]()))((list, e) => {
                          list match {
                            case Nil => e :: list
                            case x :: xs => if (x.seq < seqId) e :: list else e.copy(seq = e.seq + 1) :: list
                          }
                        }).reverse
                      Success(modList)
                    }
                  }
                  case Replace => {
                    if (!entries.map(_.seq).contains(seqId)) {
                      Failure(new Exception(s"Cannot replace: '$seqId'. Caption '$seqId' not in the sequence."))
                    } else {
                      val modList = { Entry(seqId, time._1, time._2, caption) :: entries.filter(_.seq != seqId) }.sortBy(_.seq)
                      Success(modList)
                    }
                  }
                  case _ => Success(entries) //ignore
                }
              }
          }
          bus.onNext(mod)
          goToCaptionPosition(seqId)
        }
        case _ => {
          val errorMsg = s"${Parser.extractFailureMessage(trySeqId, tryTime, tryCaption)}"
          handleError(InvalidSRTContentException(errorMsg))
        }

      }

    }

    private def setSavedInTitle(saved: Boolean) {
      //update title bar with unsaved symbol
      val currTitle = main.title.replace(syncedSymbol, "")
      if (!saved) {
        main.title = String.valueOf(currTitle + syncedSymbol)
        main.repaint()
      }
    }

    // Show the wit cursor while given code `op` is executing.
    private def waitCursor(op: => Unit) {
      val previous = cursor
      cursor = getPredefinedCursor(WAIT_CURSOR)
      try {
        op
      } finally {
        cursor = previous
      }
    }

    private def locateCaption(from: Int, searchLen: Int, searchPredicate: String => Boolean): Int = {
      @tailrec
      def find(pos: Int, doc: Document): Int = {
        if (pos + searchLen >= doc.getLength) {
          -1 //no match
        } else if (searchPredicate(doc.getText(pos, searchLen).toLowerCase)) {
          pos
        } else {
          find(pos + 1, doc) //iterate
        }
      }
      val peer = captionAllArea.peer
      val doc = peer.getDocument
      find(from, doc)
    }

    private def onSequence(seqId: Int) { if (!captionAllArea.text.isEmpty()) goToCaptionPosition(seqId) }

    private def goToCaptionPosition(seqId: Int) {
      val peer = captionAllArea.peer
      val searchLen = String.valueOf(seqId).length + 2
      waitCursor {
        locateCaption(0, searchLen, text => { s"^$seqId\n\\d+".r findFirstIn text }.isDefined) match {
          case -1 => //ignore
          case textPosition: Int => bus.onNext(Get(seqId, textPosition + 1))
        }
      }
    }

    private def serveSearchMatch() {

    }

    private def serveCaption(entry: Entry, fromTextPosition: Int) {
      //jump to its position in the text area
      log.trace(s"Serving: $entry @ position: $fromTextPosition")
      //Scroll to position
      val peer = captionAllArea.peer
      val to = fromTextPosition + entry.toString().length() - 3 //omit the new line characters
      val lineFrom = captionAllArea.peer.getLineOfOffset(fromTextPosition)
      val lineTo = captionAllArea.peer.getLineOfOffset(to)
      peer.setCaretPosition(peer.getLineStartOffset(lineTo))
      //Highlight
      highlighter.removeAllHighlights()
      highlighter.addHighlight(peer.getLineStartOffset(lineFrom), peer.getLineEndOffset(lineTo), painter)
      setCaption(entry)
      //set sequence spinner
      sequenceSpinner.value = entry.seq
    }

    private def setCaption(entry: Entry) {
      //set sidebar
      //set time slider
      timeDistanceSlider.value = entry.seq
      timeDistanceSliderLabel.text = s"Time: ${entry.begin}"
      //set caption panel components
      captionTextArea.text = entry.caption
      beginField.text = entry.begin.toString
      endField.text = entry.end.toString
      seqIdField.text = String.valueOf(entry.seq)
    }

    private def writeToFile(entries: List[Entry], filePath: String) {
      new PrintWriter(filePath) {
        write(entries.map(_.toString).mkString)
        close
      }
    }

    private def getMaxSeqId(entries: List[Entry]): Int = entries.sortBy(entry => entry.seq).reverse match {
      case Nil => 0
      case x :: xs => x.seq
    }

    //error handler 
    private def handleError(throwable: Throwable) {
      log.debug(s"handling error: $throwable")
      Dialog.showMessage(main, throwable.getMessage, "Error", Error)
    }
  }
}