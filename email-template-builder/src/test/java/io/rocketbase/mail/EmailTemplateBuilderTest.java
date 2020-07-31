package io.rocketbase.mail;

import io.rocketbase.mail.config.TbConfiguration;
import io.rocketbase.mail.model.HtmlTextEmail;
import org.junit.Test;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class EmailTemplateBuilderTest {

    private Mailer mailer;

    protected Mailer getMailer() {
        if (mailer == null) {
            // default
            mailer = MailerBuilder.withSMTPServer("localhost", 1025)
                    .buildMailer();
        }
        return mailer;
    }

    protected void sentEmail(String subject, HtmlTextEmail content) {
        try {
            Email email = EmailBuilder.startingBlank()
                    .to("melistik@icloud.com")
                    .from("service@rocketbase.io")
                    .withSubject(subject)
                    .withHTMLText(content.getHtml())
                    .withPlainText(content.getText())
                    .buildEmail();
            getMailer().sendMail(email);
        } catch (Exception e) {

            // ignore error here - works only on local machine for visible test-purpose
        }
    }

    @Test
    public void standardTestHtml() {
        // given
        EmailTemplateBuilder.EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        String header = "test";
        // when
        HtmlTextEmail htmlTextEmail = builder
                .header()
                .logo("https://www.rocketbase.io/img/logo-dark.png").logoHeight(41)
                .and()
                .text("Welcome, {{name}}!").h1().center().and()
                .text("Thanks for trying [Product Name]. We’re thrilled to have you on board. To get the most out of [Product Name], do this primary next step:").and()
                .button("Do this Next", "http://localhost").blue().and()
                .text("For reference, here's your login information:").and()
                .attribute()
                .keyValue("Login Page", "{{login_url}}")
                .keyValue("Username", "{{username}}")
                .and()
                .html("If you have any questions, feel free to <a href=\"mailto:{{support_email}}\">email our customer success team</a>. (We're lightning quick at replying.) We also offer <a href=\"{{live_chat_url}}\">live chat</a> during business hours.",
                        "If you have any questions, feel free to email our customer success team\n" +
                                "(We're lightning quick at replying.) We also offer live chat during business hours.").and()
                .text("Cheers,\n" +
                        "The [Product Name] Team").and()
                .copyright("rocketbase").url("https://www.rocketbase.io").suffix(". All rights reserved.").and()
                .footerText("[Company Name, LLC]\n" +
                        "1234 Street Rd.\n" +
                        "Suite 1234").and()
                .footerImage("https://cdn.rocketbase.io/assets/loading/no-image.jpg").width(100).linkUrl("https://www.rocketbase.io").and()
                .build();
        // then
        assertThat(htmlTextEmail, notNullValue());

        sentEmail("standardTestHtml", htmlTextEmail);
    }

    @Test
    public void standardTableTestHtml() {
        // given
        EmailTemplateBuilder.EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();

        String header = "Invoice {{invoice_id}}";
        // when
        TbConfiguration config = TbConfiguration.newInstance();
        config.getContent().setFull(true);
        HtmlTextEmail htmlTextEmail = builder
                .configuration(config)
                .header().text(header).and()
                .text("Hi {{name}},").and()
                .text("Thanks for using [Product Name]. This is an invoice for your recent purchase").and()
                .tableSimple("#.## '€'")
                .headerRow("Description", "Amount")
                .itemRow("Special Product\n" +
                        "Some extra explanations in separate line", BigDecimal.valueOf(1333, 2))
                .itemRow("Short service", BigDecimal.valueOf(103, 1))
                .footerRow("Total", BigDecimal.valueOf(2363, 2))
                .and()
                .button("Download PDF", "http://localhost").gray().right().and()
                .text("If you have any questions about this receipt, simply reply to this email or reach out to our support team for help.").and()
                .copyright("rocketbase").url("https://www.rocketbase.io").suffix(". All rights reserved.").and()
                .footerText("[Company Name, LLC]\n" +
                        "1234 Street Rd.\n" +
                        "Suite 1234").and()
                .build();
        // then
        assertThat(htmlTextEmail, notNullValue());
        sentEmail("standardTableTestHtml", htmlTextEmail);
    }

    @Test
    public void standardTableExtendedTestHtml() {
        // given
        EmailTemplateBuilder.EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();

        String header = "Invoice {{invoice_id}}";
        // when
        TbConfiguration config = TbConfiguration.newInstance();
        config.getContent().setWidth(800);

        HtmlTextEmail htmlTextEmail = builder
                .configuration(config)
                .header().text(header).and()
                .text("Hi {{name}},").and()
                .text("Thanks for using [Product Name]. This is an invoice for your recent purchase").and()
                .tableSimpleWithImage("#.## '€'")
                .headerRow("Preview", "Description", "Amount")
                .itemRow("https://cdn.shopify.com/s/files/1/0255/1211/6260/products/TCW1142-07052_small.jpg?v=1589200198", "Damen Harbour Tanktop × 1\n" +
                        "QUARTZ PINK / S", BigDecimal.valueOf(4995, 2))
                .itemRow("https://cdn.shopify.com/s/files/1/0255/1211/6260/products/TCM1886-0718_201_fdf0be52-639f-4ea8-9143-6bd75e0821b1_small.jpg?v=1583509609", "Herren ten Classic T-Shirt\n"+
                        "FOREST GREEN HEATHER / XL", BigDecimal.valueOf(3995, 2))
                .itemRow("https://cdn.shopify.com/s/files/1/0255/1211/6260/products/TCM1939-0439_1332_da6f3e7c-e18d-4778-be97-c6c0b482b643_small.jpg?v=1583509671", "Herren Joshua Hanfshorts\n" +
                        "DARK OCEAN BLUE / XL", BigDecimal.valueOf(6995, 2))
                .footerRow("Sum", BigDecimal.valueOf(15985, 2))
                .footerRow("Code - PLANT5", BigDecimal.valueOf(-799, 2))
                .footerRow("Total incl. Tax\n", BigDecimal.valueOf(15186, 2))
                .and()
                .button("Download PDF", "http://localhost").gray().right().and()
                .text("If you have any questions about this receipt, simply reply to this email or reach out to our support team for help.").and()
                .copyright("rocketbase").url("https://www.rocketbase.io").suffix(". All rights reserved.").and()
                .footerText("[Company Name, LLC]\n" +
                        "1234 Street Rd.\n" +
                        "Suite 1234").and()
                .build();
        // then
        assertThat(htmlTextEmail, notNullValue());
        sentEmail("standardTableExtendedTestHtml", htmlTextEmail);
    }

    @Test
    public void withoutHeaderAndFooterHtml() {
        // given
        EmailTemplateBuilder.EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();

        String firstText = "sample-text 1";
        String secondText = "sample-text 2";

        String button1Text = "button 1";
        String button1Url = "http://url1?test=2134&amp;bla=blub";

        String button2Text = "button 2";

        // when
        HtmlTextEmail htmlTextEmail = builder
                .text(firstText).and()
                .button(button1Text, button1Url).green().and()
                .text(secondText).and()
                .button(button2Text, "http://url2").red()
                .build();
        // then
        assertThat(htmlTextEmail, notNullValue());

        sentEmail("withoutHeaderAndFooterHtml", htmlTextEmail);
    }


    @Test
    public void standardTestText() {
        // given
        EmailTemplateBuilder.EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        String header = "test";
        String text = "sample-text";
        String buttonText = "button 1";
        String buttonUrl = "http://adasd";
        String copyrightName = "rocketbase";
        String copyrightUrl = "https://www.rocketbase.io";
        // when
        HtmlTextEmail htmlTextEmail = builder.header().text(header).and()
                .text(text).and()
                .button(buttonText, buttonUrl).and()
                .copyright(copyrightName).url(copyrightUrl)
                .build();
        // then
        assertThat(htmlTextEmail, notNullValue());
        String lineBreak = System.getProperty("line.separator");
        assertThat(htmlTextEmail.getText(), equalTo(new StringBuffer()
                .append("***************************").append(lineBreak)
                .append(header).append(lineBreak)
                .append("***************************").append(lineBreak).append(lineBreak).append(lineBreak)
                .append(text).append(lineBreak).append(lineBreak)
                .append(buttonText).append(" -> ").append(buttonUrl).append(lineBreak).append(lineBreak)
                .append("-----------").append(lineBreak).append(lineBreak).append(lineBreak)
                .append("©").append(LocalDate.now().getYear()).append(" ").append(copyrightName).append(" -> ").append(copyrightUrl)
                .append(lineBreak)
                .toString()
        ));

        sentEmail("standardTestText", htmlTextEmail);
    }

    @Test
    public void forcedText() {
        // given
        EmailTemplateBuilder.EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        // when
        HtmlTextEmail htmlTextEmail = builder
                .text("sample <b>bold</b> text &Uuml;mlaut").and()
                .build();
        // then
        assertThat(htmlTextEmail, notNullValue());
        assertThat(htmlTextEmail.getHtml(), containsString("sample &lt;b&gt;bold&lt;/b&gt; text &amp;Uuml;mlaut"));
        assertThat(htmlTextEmail.getText(), containsString("sample <b>bold</b> text &Uuml;mlaut"));

        sentEmail("forcedText", htmlTextEmail);
    }

    @Test
    public void forcedHtml() {
        // given
        EmailTemplateBuilder.EmailTemplateConfigBuilder builder = EmailTemplateBuilder.builder();
        // when
        HtmlTextEmail htmlTextEmail = builder
                .html("sample <b>bold</b> text &Uuml;mlaut &lt; 17", "sample bold text Ümlaut < 17").and()
                .build();
        // then
        assertThat(htmlTextEmail, notNullValue());
        assertThat(htmlTextEmail.getHtml(), containsString("sample <b>bold</b> text &Uuml;mlaut &lt; 17"));
        assertThat(htmlTextEmail.getText(), containsString("sample bold text Ümlaut < 17"));

        sentEmail("forcedHtml", htmlTextEmail);
    }
}