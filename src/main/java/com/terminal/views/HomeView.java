package com.terminal.views;

import com.webforj.component.Composite;
import com.webforj.component.Theme;
import com.webforj.component.button.Button;
import com.webforj.component.button.ButtonTheme;
import com.webforj.component.field.TextField;
import com.webforj.component.html.elements.H1;
import com.webforj.component.html.elements.Paragraph;
import com.webforj.component.icons.FeatherIcon;
import com.webforj.component.layout.flexlayout.FlexDirection;
import com.webforj.component.layout.flexlayout.FlexLayout;
import com.webforj.component.toast.Toast;
import com.webforj.router.annotation.Route;
import com.webforj.router.Router;

@Route("/")
public class HomeView extends Composite<FlexLayout> {

  private FlexLayout self = getBoundComponent();
  private H1 title = new H1("webforJ Terminal Demo");
  private Paragraph description = new Paragraph("Experience the power of terminal applications in the browser!");
  private Button terminalBtn = new Button("Open Terminal");

  public HomeView() {
    self.setDirection(FlexDirection.COLUMN);
    self.setMaxWidth(400);
    self.setStyle("margin", "2em auto");
    self.setStyle("text-align", "center");

    terminalBtn.setPrefixComponent(FeatherIcon.TERMINAL.create())
        .setTheme(ButtonTheme.SUCCESS)
        .setStyle("margin-bottom", "2em")
        .addClickListener(e -> Router.getCurrent().navigate(TerminalView.class));

    self.add(title, description, terminalBtn);
  }
}
