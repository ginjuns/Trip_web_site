document.addEventListener('DOMContentLoaded', function () {

    const calendarEl = document.getElementById('calendar');

    const calendar = new FullCalendar.Calendar(calendarEl, {

        initialView: 'dayGridMonth',

        locale: 'ko',

        fixedWeekCount: false,

        showNonCurrentDates: true,

        expandRows: false,

        height: 'auto',

        dayCellDidMount: function(info) {

            const year =
                info.date.getFullYear();

            const month =
                String(info.date.getMonth() + 1)
                    .padStart(2, "0");

            const day =
                String(info.date.getDate())
                    .padStart(2, "0");

            const dateStr =
                `${year}-${month}-${day}`;

            fetch(
                "/weather/calendar?date="
                + dateStr
            )

                .then(response => response.json())

                .then(data => {

                    const iconDiv =
                        document.createElement("div");

                    iconDiv.innerHTML =
                        data.icon;

                    iconDiv.style.fontSize =
                        "22px";

                    iconDiv.style.textAlign =
                        "center";

                    iconDiv.style.marginTop =
                        "4px";

                    iconDiv.style.marginBottom = "4px";

                    const dayTop =
                        info.el.querySelector(
                            ".fc-daygrid-day-top"
                        );

                    if(dayTop){

                        dayTop.after(iconDiv);

                    }

                });

        },

        dateClick: function(info) {

            document.getElementById(
                "modalContent"
            ).innerHTML =

                "<h5>"
                + info.dateStr
                + "</h5>"
                + "<p>날씨 불러오는 중...</p>";

            fetch(
                "/weather/detail?date="
                + info.dateStr
            )

                .then(response => response.json())

                .then(data => {

                    document.getElementById(
                        "modalContent"
                    ).innerHTML =

                        "<h4 class='mb-3'>📅 "
                        + data.date
                        + "</h4>"

                        + data.message;

                });

            const modal =
                new bootstrap.Modal(
                    document.getElementById(
                        "weatherModal"
                    )
                );

            modal.show();

        }

    });

    calendar.render();

});