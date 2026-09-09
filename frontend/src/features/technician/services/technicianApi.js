import { api } from '../../../common/api/api'
import { API_ROUTES } from '../../../common/api/apiRoutes'

export function fetchAvailableTechnicians({ date, start, end, durationMinutes }) {
    const params = new URLSearchParams({ date, start, end, durationMinutes });
    return api(`${API_ROUTES.TECHNICIANS}/available?${params.toString()}`);
}